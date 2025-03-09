package org.eclipse.m2m.internal.qvt.oml.debug.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
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
import org.eclipse.m2m.qvt.oml.debug.core.QVTOStackFrame;
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
		
		var stackFrame = getCurrentStackFrame();
		if (stackFrame == null) {
			return CompletableFuture.completedFuture(List.of());
		}

		QvtEditor qvtEditor = (QvtEditor) super.getAdapter(ITextEditor.class);

		if (qvtEditor == null) {
			return CompletableFuture.completedFuture(List.of());
		}
		
		List<IVariable> variables;
		try {
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

		Set<ObjectExp> objectExpressions = new HashSet<>();
		Set<MappingCallExp> mappingCallExpressions = new HashSet<>();

		List<ICodeMining> codeMinings = new ArrayList<>();

		QvtOperationalAstWalker walker = new QvtOperationalAstWalker(new QvtOperationalAstWalker.NodeProcessor() {

			@Override
			public void process(Visitable visitable, Visitable parent) {
				if (!(visitable instanceof ASTNode astNode)) {
					return;
				}
				
				addModelParameterCodeMinings(astNode, variables, codeMinings);

				if (astNode.getStartPosition() > finalLineEndOffset || astNode.getEndPosition() < finalLineStartOffset) {
					return;
				}

				if (astNode instanceof ObjectExp objectExpression) {
					var name = objectExpression.getName();

					// only look at temp variables as otherwise we would create code-minings for the
					// result variable as the bodies of mappings are also object expressions
					if (!name.startsWith(QvtEnvironmentBase.GENERATED_NAME_SPECIAL_PREFIX + "temp")) {
						return;
					}

					if (variableOfNameExists(variables, name)) {
						objectExpressions.add(objectExpression);
						return;
					}
				} else if (astNode instanceof MappingCallExp mappingCallExp) {
					var variableName = ((VariableExp<?, ?>) mappingCallExp.getSource()).getName();

					if (!variableName.startsWith("temp")) {
						return;
					}

					if (variableOfNameExists(variables, variableName)) {
						mappingCallExpressions.add(mappingCallExp);
						return;
					}
				}
			}
		});

		for (var module : compiledUnit.getModules()) {
			module.accept(walker);
		}
		
		// create code-minings for the found object expressions and mapping call
		// expressions
		for (var objectExpression : objectExpressions) {
			var label = objectExpression.getName() + " : ";
			int start = objectExpression.getStartPosition() + "object".length() + 1;
			var codeMining = new DebugCodeMining(label, start, 1, this);
			codeMinings.add(codeMining);
		}

		for (var mappingCallExpression : mappingCallExpressions) {
			var variable = (VariableExp<?, ?>) mappingCallExpression.getSource();
			var label = variable.getName() + ".";
			int start = mappingCallExpression.getStartPosition() + "map".length() + 1;
			var codeMining = new DebugCodeMining(label, start, 1, this);
			codeMinings.add(codeMining);
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

	private QVTOStackFrame getCurrentStackFrame() {
		var debugContext = DebugUITools.getDebugContext();
		if (debugContext == null) {
			return null;
		}
		return debugContext.getAdapter(QVTOStackFrame.class);
	}

}
