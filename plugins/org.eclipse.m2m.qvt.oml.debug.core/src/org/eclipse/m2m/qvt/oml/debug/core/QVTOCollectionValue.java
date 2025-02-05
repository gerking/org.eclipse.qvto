package org.eclipse.m2m.qvt.oml.debug.core;

import java.util.Arrays;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IIndexedValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.m2m.qvt.oml.debug.core.vm.VMVariable;

public class QVTOCollectionValue extends QVTOValue implements IIndexedValue {

	QVTOCollectionValue(IQVTODebugTarget debugTarget, VMVariable vmVar, long frameID) {
		super(debugTarget, vmVar, frameID);
	}

	@Override
	public IVariable getVariable(int offset) throws DebugException {
		return super.getVariables()[offset];
	}

	@Override
	public IVariable[] getVariables(int offset, int length) throws DebugException {
		return Arrays.copyOfRange(super.getVariables(), offset, offset + length);
	}

	@Override
	public int getSize() throws DebugException {
		return super.getVariables().length;
	}

	@Override
	public int getInitialOffset() {
		return 0;
	}

}
