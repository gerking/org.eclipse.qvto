/*******************************************************************************
 * Copyright (c) 2008, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 583587
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.evaluator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.DynamicValueHolder;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.SettingDelegate;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.m2m.internal.qvt.oml.ast.env.ModelParameterExtent;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.IntermediateClassFactory;
import org.eclipse.m2m.internal.qvt.oml.evaluator.TransformationInstance.InternalTransformation;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelParameter;
import org.eclipse.m2m.internal.qvt.oml.expressions.ModelType;
import org.eclipse.m2m.internal.qvt.oml.expressions.OperationalTransformation;
import org.eclipse.m2m.internal.qvt.oml.stdlib.CallHandler;
import org.eclipse.m2m.internal.qvt.oml.stdlib.QVTUMLReflection;


class TransformationInstanceImpl extends ModuleInstanceImpl implements TransformationInstance, InternalTransformation {

	private final Map<ModelParameter, ModelInstance> fModelParams;
	private ModelInstance fIntermediateData;
	private CallHandler fTransHandler;
	private SettingDelegate fSettingDelegate = new SettingDelegate() {
		
		@Override
		public void dynamicUnset(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID) {
			TransformationInstanceImpl.this.dynamicUnset(dynamicFeatureID);
		}

		@Override
		public void dynamicSet(InternalEObject owner, EStructuralFeature.Internal.DynamicValueHolder settings, int dynamicFeatureID, Object newValue) {						
			TransformationInstanceImpl.this.dynamicSet(dynamicFeatureID, newValue);
		}

		@Override
		public boolean dynamicIsSet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID) {
			return TransformationInstanceImpl.this.dynamicGet(dynamicFeatureID) != null;
		}
		
		@Override
		public Object dynamicGet(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID, boolean resolve, boolean coreType) {
			EStructuralFeature feature = owner.eClass().getEStructuralFeature(dynamicFeatureID);
			Object value = TransformationInstanceImpl.this.dynamicGet(dynamicFeatureID);
			
			if (value == null) {
				return feature.getDefaultValue();
			}
			else if (value == NIL) {
				return null;
			}
			else {
				return value;
			}
		}		
		
		@Override
		public Setting dynamicSetting(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID) {
			throw new UnsupportedOperationException();
		}
				
		@Override
		public NotificationChain dynamicInverseRemove(InternalEObject owner, DynamicValueHolder settings,
				int dynamicFeatureID, InternalEObject otherEnd, NotificationChain notifications) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public NotificationChain dynamicInverseAdd(InternalEObject owner, DynamicValueHolder settings, int dynamicFeatureID,
				InternalEObject otherEnd, NotificationChain notifications) {
			throw new UnsupportedOperationException();
		}
	};
	
	TransformationInstanceImpl(OperationalTransformation type) {
		super(type);
		fModelParams = new HashMap<ModelParameter, ModelInstance>(3);
		initIntermediateExtentIfRequired();		
	}
	
	public void setModel(ModelParameter parameter, ModelInstance extent) {
		if(parameter == null || extent == null) {
			throw new IllegalArgumentException();
		}

		fModelParams.put(parameter, extent);
	}
	
	public ModelInstance getIntermediateExtent() {	
		return fIntermediateData;
	}

	public OperationalTransformation getTransformation() {	
		return (OperationalTransformation) eClass();
	}
	
	public ModelInstance getModel(ModelParameter modelParam) {
		return fModelParams.get(modelParam);
	}
	
	@Override
	public <T> T getAdapter(Class<T> adapterType) {
		if(InternalTransformation.class == adapterType || 
			TransformationInstance.class == adapterType ||
			ModuleInstance.class == adapterType) {
			return adapterType.cast(this);
		}

		return super.getAdapter(adapterType);
	}
	
	public CallHandler getTransformationHandler() {
		return fTransHandler;
	}
	
	public void setTransformationHandler(CallHandler handler) {
		fTransHandler = handler;
	}
	
	@Override
	public void dispose() {	
		super.dispose();
	}

	@Override
	public String toString() {
		OperationalTransformation transformation = getTransformation();
		
		StringBuilder buf = new StringBuilder();
		buf.append("transformation ").append(transformation.getName()).append("("); //$NON-NLS-1$ //$NON-NLS-2$
		
		int pos = 0;
		for (ModelParameter modelParameter : transformation.getModelParameter()) {
			if(pos++ > 0) {
				buf.append(',').append(' ');
			}
			buf.append(modelParameter.getKind()).append(' ')
				.append(modelParameter.getName())
				.append(" : "); //$NON-NLS-1$
			
			EClassifier type = modelParameter.getEType();
			if(type != null) {
				buf.append(type.getName());
			}
		}
		
		buf.append(") @").append(Integer.toHexString(System.identityHashCode(this))); //$NON-NLS-1$
		return buf.toString();
	}

	private void initIntermediateExtentIfRequired() {
		for (EClassifier ownedType : getTransformation().getEClassifiers()) {
			if(ownedType instanceof ModelType) {
				ModelType modelType = (ModelType) ownedType;			
				if(IntermediateClassFactory.isIntermediateModelType(modelType)) {
					fIntermediateData = new ModelInstanceImpl(modelType, new ModelParameterExtent());
					return;
				}
			}
		}
	}
			
	@Override
	protected SettingDelegate eSettingDelegate(EStructuralFeature eFeature) {
						
		if (!QVTUMLReflection.isUserModelElement(eFeature.getEType())) {			
			return fSettingDelegate;
		};
		
		return super.eSettingDelegate(eFeature);
	}
}
