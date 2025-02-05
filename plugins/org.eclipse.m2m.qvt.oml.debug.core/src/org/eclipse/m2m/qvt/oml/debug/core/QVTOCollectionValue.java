/*******************************************************************************
 * Copyright (c) 2026 S. Steudle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Steffen Steudle - initial API and implementation
 *******************************************************************************/
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
		return getVariables()[offset];
	}

	@Override
	public IVariable[] getVariables(int offset, int length) throws DebugException {
		return Arrays.copyOfRange(getVariables(), offset, offset + length);
	}

	@Override
	public int getSize() throws DebugException {
		return getVariables().length;
	}

	@Override
	public int getInitialOffset() {
		return 0;
	}

}
