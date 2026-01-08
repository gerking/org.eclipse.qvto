package org.eclipse.m2m.internal.qvt.oml.debug.ui;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.hover.ExpressionInformationControlCreator;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOStackFrame;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOVariable;
import org.eclipse.m2m.qvt.oml.editor.ui.hovers.IElementInfoProvider;
import org.eclipse.ocl.cst.SimpleNameCS;

public class QVTODebugElementInfoProvider implements IElementInfoProvider {

	@Override
	public Object getElementInfo(Object element, ITextViewer textViewer, IRegion region) {
		var debugContext = DebugUITools.getDebugContext();
		if (debugContext == null) {
			return null;
		}

		if (!(element instanceof SimpleNameCS simpleName)) {
			return null;
		}


		var stackFrame = debugContext.getAdapter(QVTOStackFrame.class);

		try {
			return getQVTOVariableFor(simpleName, stackFrame);
		} catch (DebugException e) {
			return null;
		}
	}

	@SuppressWarnings("restriction")
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return new ExpressionInformationControlCreator();
	}

	private static QVTOVariable getQVTOVariableFor(SimpleNameCS simpleNameCS, QVTOStackFrame startingStackFrame) throws DebugException {
		for (var stackFrame : startingStackFrame.getThread().getStackFrames()) {
			for (var variable : stackFrame.getVariables()) {
				QVTOVariable qvtoVariable = tryGetQVTOVariableFor(simpleNameCS, variable);

				if (qvtoVariable != null) {
					return qvtoVariable;
				}

				if (variable.getName().equals("result")) {
					for (IVariable resultVariable : variable.getValue().getVariables()) {
						QVTOVariable resultQvtoVariable = tryGetQVTOVariableFor(simpleNameCS, resultVariable);

						if (resultQvtoVariable != null) {
							return resultQvtoVariable;
						}
					}
				}
			}
		}

		return null;
	}

	private static QVTOVariable tryGetQVTOVariableFor(SimpleNameCS simpleNameCS, IVariable variable) {
		if (!(variable instanceof QVTOVariable qvtoVariable)) {
			return null;
		}

		if (qvtoVariable.getName().equals(simpleNameCS.getValue())) {
			return qvtoVariable;
		}

		return null;
	}

	@Override
	public int getPriority() {
		return 1;
	}
}
