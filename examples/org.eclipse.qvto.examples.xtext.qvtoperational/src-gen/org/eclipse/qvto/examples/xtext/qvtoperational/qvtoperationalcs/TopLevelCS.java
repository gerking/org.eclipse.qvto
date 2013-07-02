/**
 */
package org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.examples.xtext.base.util.VisitableCS;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Top Level CS</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>
 * {@link org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.TopLevelCS#getImport
 * <em>Import</em>}</li>
 * <li>
 * {@link org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.TopLevelCS#getUnit
 * <em>Unit</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.QvtoperationalcsPackage#getTopLevelCS()
 * @model superTypes="org.eclipse.ocl.examples.xtext.base.baseCST.VisitableCS"
 * @generated
 */
public interface TopLevelCS
		extends EObject, VisitableCS {

	/**
	 * Returns the value of the '<em><b>Import</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.ImportCS}
	 * . <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Import</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Import</em>' containment reference list.
	 * @see org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.QvtoperationalcsPackage#getTopLevelCS_Import()
	 * @model containment="true"
	 * @generated
	 */
	EList<ImportCS> getImport();

	/**
	 * Returns the value of the '<em><b>Unit</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.UnitElementCS}
	 * . <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Unit</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Unit</em>' containment reference list.
	 * @see org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.QvtoperationalcsPackage#getTopLevelCS_Unit()
	 * @model containment="true"
	 * @generated
	 */
	EList<UnitElementCS> getUnit();

} // TopLevelCS
