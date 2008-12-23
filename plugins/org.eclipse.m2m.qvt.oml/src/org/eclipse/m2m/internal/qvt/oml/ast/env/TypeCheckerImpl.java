/*******************************************************************************
 * Copyright (c) 2008 Borland Software Corporation
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.ast.env;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.expressions.ListType;
import org.eclipse.ocl.AbstractTypeChecker;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.types.CollectionType;
import org.eclipse.ocl.types.TupleType;
import org.eclipse.ocl.util.TypeUtil;
import org.eclipse.ocl.utilities.TypedElement;
import org.eclipse.ocl.utilities.UMLReflection;

class TypeCheckerImpl extends AbstractTypeChecker<EClassifier, EOperation, EStructuralFeature, EParameter> {
		
	private final GenericsResolver fGenericResolver;
	
	TypeCheckerImpl(Environment<?, EClassifier, EOperation, EStructuralFeature, ?, EParameter, ?, ?, ?, ?, ?, ?> env) {
		super(env);
		fGenericResolver = new GenericsResolver((EcoreEnvironment)env);
	}

	@Override
	public boolean isStandardLibraryFeature(EClassifier owner, Object feature) {
		if(feature instanceof EOperation && isQVTOperation((EOperation) feature)) {
			return false;
		}
		
		return super.isStandardLibraryFeature(owner, feature);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected CollectionType<EClassifier, EOperation> resolveCollectionType(CollectionKind kind,
			EClassifier elementType) {
		return (CollectionType<EClassifier, EOperation>) TypeUtil.resolveCollectionType(
			getEnvironment(), kind, elementType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected TupleType<EOperation, EStructuralFeature> resolveTupleType(
			EList<? extends TypedElement<EClassifier>> parts) {
		return (TupleType<EOperation, EStructuralFeature>) TypeUtil.resolveTupleType(getEnvironment(), parts);
	}
	
	@Override
	protected EClassifier resolve(EClassifier type) {		
		return TypeUtil.resolveType(getEnvironment(), type);
	}

	@Override
	public List<EOperation> getOperations(EClassifier owner) {
		if(owner instanceof ListType) {			
			EClassifier list = QvtOperationalStdLibrary.INSTANCE.getList();
			if(owner != list) {
				return TypeUtil.getOperations(getEnvironment(), list);
			}
		}
		return super.getOperations(owner);
	}
	
	@Override
	public EClassifier getResultType(Object problemObject,
			EClassifier owner, EOperation operation,
			List<? extends TypedElement<EClassifier>> args) {

		if(isQVTOperation(operation)) { 
			return fGenericResolver.resolveOperationReturnType(owner, operation, args);				
		}
		
		return super.getResultType(problemObject, owner, operation, args);
	}

	@Override
	public int getRelationship(EClassifier type1, EClassifier type2) {
		boolean isList1 = type1 instanceof ListType;
		boolean isList2 = type2 instanceof ListType;
		
		if(isList1 || isList2) {
			// handle list types
			if(isList1 && isList2) {
				ListType list1 = (ListType) type1;
				ListType list2 = (ListType) type2;
				return getRelationship(list1.getElementType(), list2.getElementType());
			} else if(!isList1 && type1 instanceof CollectionType) {
				@SuppressWarnings("unchecked")
				CollectionType<EClassifier, EOperation> col1 = (CollectionType<EClassifier, EOperation>) type1;

				if(col1.getKind() == CollectionKind.COLLECTION_LITERAL &&
					TypeUtil.compatibleTypeMatch(getEnvironment(), 
						((ListType)type2).getElementType(), col1.getElementType())) {
					return UMLReflection.STRICT_SUPERTYPE;
				}				
			} else if(!isList2 && type2 instanceof CollectionType) {
				@SuppressWarnings("unchecked")
				CollectionType<EClassifier, EOperation> col2 = (CollectionType<EClassifier, EOperation>) type2;

				if(col2.getKind() == CollectionKind.COLLECTION_LITERAL &&
					TypeUtil.compatibleTypeMatch(getEnvironment(), 
						((ListType)type1).getElementType(), col2.getElementType())) {
					return UMLReflection.STRICT_SUBTYPE	;
				}				
			}
			
			return UMLReflection.UNRELATED_TYPE;
		} 
		return super.getRelationship(type1, type2);
	}
	
	private boolean isQVTOperation(EOperation operation) {
		return QvtOperationalParserUtil.getOwningModule(operation) != null;
	}
}