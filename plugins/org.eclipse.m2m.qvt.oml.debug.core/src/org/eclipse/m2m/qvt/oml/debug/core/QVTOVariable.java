/*******************************************************************************
 * Copyright (c) 2009, 2018 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.debug.core;

import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.m2m.qvt.oml.debug.core.vm.VMVariable;
import org.eclipse.m2m.qvt.oml.debug.core.vm.Value;

public class QVTOVariable extends QVTODebugElement implements IVariable {
	
	final VMVariable vmVar;
	private final long fFrameID;
	protected IValue fValue;

	
	QVTOVariable(IQVTODebugTarget debugTarget, final VMVariable vmVar, long frameID) {
		super(debugTarget);
		
		this.vmVar = vmVar;			
		this.fFrameID = frameID;
	}

	public boolean isModelParameter() {
		return vmVar.kind == VMVariable.MODEL_PARAMETER;
	}
	
	public boolean isLocalVariable() {
		return vmVar.kind == VMVariable.LOCAL;
	}
	
	public boolean isCollectionElement() {
		return vmVar.kind == VMVariable.COLLECTION_ELEMENT;
	}	

	public boolean isPredefinedVariable() {
		return vmVar.kind == VMVariable.PREDEFINED_VAR;
	}		
	
	public boolean isIntermProperty() {
		return vmVar.kind == VMVariable.INTERM_PROPERTY;
	}	

	public boolean isAttribute() {
		return vmVar.kind == VMVariable.ATTRIBUTE;
	}	
	
	public boolean isReference() {
		return vmVar.kind == VMVariable.REFERENCE;
	}
	
	public IValue getValue() {
		if (fValue == null) {
			if (vmVar.type.kind == Value.Type.COLLECTION) {
				fValue = new QVTOCollectionValue(getQVTODebugTarget(), vmVar, fFrameID);
			} else {
				fValue = new QVTOValue(getQVTODebugTarget(), vmVar, fFrameID);
			}
		}
		return fValue;
	}

	public String getName() {
		return vmVar.name;
	}
	
	public String getReferenceTypeName() {
		return this.vmVar.type.declaringType;
	}	

	public boolean hasValueChanged() {
		return false;
	}

	public boolean supportsValueModification() {
		return false;
	}

	public void setValue(String expression) {
	}

	public void setValue(IValue value) {
	}

	public boolean verifyValue(String expression) {
		return false;
	}

	public boolean verifyValue(IValue value) {
		return false;
	}	
}