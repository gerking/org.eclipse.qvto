package org.eclipse.m2m.internal.qvt.oml.debug.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.source.ISourceViewerExtension5;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtEnvironmentBase;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalAstWalker;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.editor.ui.QvtEditor;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.ObjectExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.qvt.oml.debug.core.QVTODebugCore;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOStackFrame;
import org.eclipse.ocl.ecore.LoopExp;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.ASTNode;
import org.eclipse.ocl.utilities.Visitable;
import org.eclipse.ui.texteditor.ITextEditor;

public class DebugCodeMiningProvider extends AbstractCodeMiningProvider {

	private IDebugEventSetListener debugListener;

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer, IProgressMonitor monitor) {
		ensureDebugListener(viewer);

		if (!hasDebugRunning()) {
			return CompletableFuture.completedFuture(List.of());
		}
		
		QvtEditor qvtEditor = (QvtEditor) super.getAdapter(ITextEditor.class);

		if (qvtEditor == null) {
			return CompletableFuture.completedFuture(List.of());
		}
		
		QVTOStackFrame stackFrame;
		List<IVariable> variables;
		try {
			stackFrame = getCurrentStackFrame();
			if (stackFrame == null) {
				return CompletableFuture.completedFuture(List.of());
			}
			variables = Arrays.asList(stackFrame.getVariables());
		} catch (DebugException e) {
			return CompletableFuture.completedFuture(List.of());
		}

		// stack frame line number is 1-based
		int lineNumber = stackFrame.getLineNumber() - 1;
		
		IDocument document = qvtEditor.getDocumentProvider().getDocument(qvtEditor.getEditorInput());

		int lineStartOffset;
		try {
			lineStartOffset = document.getLineOffset(lineNumber);
		} catch (BadLocationException e) {
			lineStartOffset = -1;
		}
		int lineEndOffset;
		try {
			lineEndOffset = lineStartOffset + document.getLineLength(lineNumber);
		} catch (BadLocationException e) {
			lineEndOffset = -1;
		}

		final int finalLineStartOffset = lineStartOffset;
		final int finalLineEndOffset = lineEndOffset;

		CompiledUnit compiledUnit = qvtEditor.getQVTDocumentProvider().getCompiledModule();

		if (compiledUnit == null) {
			return CompletableFuture.completedFuture(List.of());
		}

		List<ICodeMining> codeMinings = new ArrayList<>();

		QvtOperationalAstWalker walker = new QvtOperationalAstWalker(new QvtOperationalAstWalker.NodeProcessor() {

			@Override
			public void process(Visitable visitable, Visitable parent) {
				if (!(visitable instanceof ASTNode astNode)) {
					return;
				}
				
				addModelParameterCodeMinings(astNode, variables, codeMinings);

				// ignore nodes that are not in scope of the current debug line
				int startPosition = astNode.getStartPosition();
				int endPosition = astNode.getEndPosition();
				if (startPosition >= 0 && endPosition >= 0) {
					if (startPosition > finalLineEndOffset || endPosition < finalLineStartOffset) {
						return;
					}
				}

				addObjectExpressionCodeMinings(astNode, variables, codeMinings);
				addMappingCallCodeMinings(astNode, variables, codeMinings);
				addLoopExpressionCodeMinings(astNode, variables, codeMinings, document);
			}
		});

		for (var module : compiledUnit.getModules()) {
			module.accept(walker);
		}
		
		return CompletableFuture.completedFuture(codeMinings);
	}

	private void addModelParameterCodeMinings(ASTNode astNode, List<IVariable> variables, List<ICodeMining> codeMinings) {
		if (!(astNode instanceof OperationalTransformation transformation)) {
			return;
		}

		for (var modelParameter : transformation.getModelParameter()) {
			var name = modelParameter.getName();
			if (!name.matches("\\$\\d+")) {
				continue;
			}

			var label = name + " : ";
			int start = modelParameter.getStartPosition() + modelParameter.getKind().getLiteral().length() + 1;
			var codeMining = new DebugCodeMining(label, start, 1, this);
			codeMinings.add(codeMining);
		}
	}

	private void addObjectExpressionCodeMinings(ASTNode astNode, List<IVariable> variables, List<ICodeMining> codeMinings) {
		if (!(astNode instanceof ObjectExp objectExpression)) {
			return;
		}

		var name = objectExpression.getName();

		// only look at temp variables as otherwise we would create code-minings for the
		// result variable as the bodies of mappings are also object expressions
		if (!name.startsWith(QvtEnvironmentBase.GENERATED_NAME_SPECIAL_PREFIX + "temp")) {
			return;
		}

		if (variableOfNameExists(variables, name)) {
			var label = name + " : ";
			int start = objectExpression.getStartPosition() + "object".length() + 1;
			var codeMining = new DebugCodeMining(label, start, 1, this);
			codeMinings.add(codeMining);
		}
	}

	private void addMappingCallCodeMinings(ASTNode astNode, List<IVariable> variables, List<ICodeMining> codeMinings) {
		if (!(astNode instanceof MappingCallExp mappingCallExpression)) {
			return;
		}

		if (!(mappingCallExpression.getSource() instanceof VariableExp<?, ?> variableExp)) {
			return;
		}

		var variableName = variableExp.getName();

		if (!variableName.startsWith("temp")) {
			return;
		}

		if (variableOfNameExists(variables, variableName)) {
			var label = variableName + ".";
			int start = mappingCallExpression.getStartPosition() + "map".length() + 1;
			var codeMining = new DebugCodeMining(label, start, 1, this);
			codeMinings.add(codeMining);
		}
	}

	private void addLoopExpressionCodeMinings(ASTNode astNode, List<IVariable> variables, List<ICodeMining> codeMinings, IDocument document) {
		if (!(astNode instanceof LoopExp loopExp)) {
			return;
		}

		if (loopExp.getBody() instanceof MappingCallExp) {
			// mapping calls are handled separately
			return;
		}

		if (loopExp.getIterator().size() != 1) {
			// only single variable loops can have anonymous iterators
			return;
		}

		var variable = loopExp.getIterator().get(0);
		if (!variable.getName().startsWith("temp")) {
			return;
		}

		if (variableOfNameExists(variables, variable.getName())) {
			String type = variable.getType().getName();
			String label = variable.getName() + " : " + type;

			int start;
			if (variable.getStartPosition() != -1) {
				start = variable.getStartPosition();
			} else {
				// the variable does not have a start position, so we have to approximate it
				// for this we first calculate the minimum start position of the loop expression
				// and then search for the next opening parenthesis
				// +2 for the "->", +1 to be one before the opening parenthesis
				int minStart = loopExp.getSource().getEndPosition() + 2 + loopExp.getName().length() + 1;
				var findReplaceAdapter = new FindReplaceDocumentAdapter(document);
				try {
					var region = findReplaceAdapter.find(minStart, "(", true, false, false, false);
					start = region.getOffset() + 1;
				} catch (BadLocationException e) {
					// should probably not happen, but just a fallback assume that the opening
					// parenthesis is right after the loop expression name
					start = minStart + 1;
				}
			}

			if (loopExp.getStartPosition() > start || loopExp.getEndPosition() < start) {
				// Dont't add code mining if the start position is not in the loop expression
				return;
			}

			var codeMining = new DebugCodeMining(label, start, 1, this);
			codeMinings.add(codeMining);
		}
	}

	@Override
	public void dispose() {
		if (debugListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(debugListener);
			debugListener = null;
		}
	}

	private boolean variableOfNameExists(List<IVariable> variables, String name) {
		return variables.stream().anyMatch(variable -> {
			try {
				return variable.getName().equals(name);
			} catch (DebugException e) {
				return false;
			}
		});
	}

	private boolean hasDebugRunning() {
		return Stream.of(DebugPlugin.getDefault().getLaunchManager().getLaunches()).filter(Predicate.not(ILaunch::isTerminated))
				.anyMatch(launch -> ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode()));
	}

	private void ensureDebugListener(ITextViewer textViewer) {
		if (debugListener == null && textViewer instanceof ISourceViewerExtension5 sourceViewerExtension5) {
			debugListener = event -> {
				sourceViewerExtension5.updateCodeMinings();
			};
			DebugPlugin.getDefault().addDebugEventListener(debugListener);
		}
	}

	private QVTOStackFrame getCurrentStackFrame() throws DebugException {
		var debugContext = DebugUITools.getDebugContext();
		if (debugContext != null) {
			var stackFrame = debugContext.getAdapter(QVTOStackFrame.class);
			if (stackFrame != null) {
				return stackFrame;
			}
		}

		var runningLaunches = Stream.of(DebugPlugin.getDefault().getLaunchManager().getLaunches()) //
				.filter(Predicate.not(ILaunch::isTerminated)) //
				.filter(launch -> ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) //
				.filter(launch -> launch.getDebugTarget().getModelIdentifier().equals(QVTODebugCore.MODEL_ID)) //
				.collect(Collectors.toList());

		if (runningLaunches.size() != 1) {
			return null;
		}

		ILaunch launch = runningLaunches.get(0);
		var debugTarget = launch.getDebugTarget();

		if (debugTarget.getThreads().length == 0) {
			return null;
		}

		var thread = debugTarget.getThreads()[0];
		if (thread.getStackFrames().length == 0) {
			return null;
		}

		return thread.getStackFrames()[0].getAdapter(QVTOStackFrame.class);
	}

}
