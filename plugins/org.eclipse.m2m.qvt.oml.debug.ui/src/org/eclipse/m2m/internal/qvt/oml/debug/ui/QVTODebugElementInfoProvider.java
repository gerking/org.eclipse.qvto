package org.eclipse.m2m.internal.qvt.oml.debug.ui;

import java.util.Optional;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOStackFrame;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOValue;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOVariable;
import org.eclipse.m2m.qvt.oml.editor.ui.hovers.IElementInfoProvider;
import org.eclipse.ocl.cst.SimpleNameCS;

public class QVTODebugElementInfoProvider implements IElementInfoProvider {

	@Override
	public String getElementInfo(Object element, ITextViewer textViewer, IRegion region) {
		var debugContext = DebugUITools.getDebugContext();
		if (debugContext == null) {
			return null;
		}

		if (!(element instanceof SimpleNameCS simpleName)) {
			return null;
		}


		var stackFrame = debugContext.getAdapter(QVTOStackFrame.class);

		try {
			Optional<QVTOVariable> qvtoVariable = getQVTOVariableFor(simpleName, stackFrame);
			if (qvtoVariable.isEmpty()) {
				return null;
			}
			var qvtoValue = (QVTOValue) qvtoVariable.orElseThrow().getValue();
			var detail = qvtoValue.computeDetail();
			return detail;
		} catch (DebugException e) {
			return null;
		}
	}

	private static Optional<QVTOVariable> getQVTOVariableFor(SimpleNameCS simpleNameCS, QVTOStackFrame startingStackFrame) throws DebugException {
		for (var stackFrame : startingStackFrame.getThread().getStackFrames()) {
			for (var variable : stackFrame.getVariables()) {
				Optional<QVTOVariable> optionalQvtoVariable = tryGetQVTOVariableFor(simpleNameCS, variable);

				if (optionalQvtoVariable.isPresent()) {
					return optionalQvtoVariable;
				}

				if (variable.getName().equals("result")) {
					for (IVariable resultVariable : variable.getValue().getVariables()) {
						Optional<QVTOVariable> optionalResultQvtoVariable = tryGetQVTOVariableFor(simpleNameCS, resultVariable);

						if (optionalResultQvtoVariable.isPresent()) {
							return optionalResultQvtoVariable;
						}
					}
				}
			}
		}

		return Optional.empty();
	}

	private static Optional<QVTOVariable> tryGetQVTOVariableFor(SimpleNameCS simpleNameCS, IVariable variable) throws DebugException {
		if (!(variable instanceof QVTOVariable qvtoVariable)) {
			return Optional.empty();
		}

		if (qvtoVariable.getName().equals(simpleNameCS.getValue())) {
			return Optional.of(qvtoVariable);
		}

		return Optional.empty();
	}

	@Override
	public int getPriority() {
		return 1;
	}
}
