/*
 * «codeGenHelper.getCopyright(' * ')»
 *************************************************************************
 * This code is 100% auto-generated
 * using: org.eclipse.ocl.examples.codegen.java.JavaStream
 *
 * Do not edit it.
 */

package org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.util.evaluators;

import java.util.List;
import org.eclipse.ocl.examples.domain.evaluation.DomainEvaluator;
import org.eclipse.ocl.examples.domain.ids.ClassId;
import org.eclipse.ocl.examples.domain.ids.CollectionTypeId;
import org.eclipse.ocl.examples.domain.ids.IdManager;
import org.eclipse.ocl.examples.domain.ids.NsURIPackageId;
import org.eclipse.ocl.examples.domain.ids.TypeId;
import org.eclipse.ocl.examples.xtext.base.baseCST.BaseCSTPackage;
import org.eclipse.ocl.examples.xtext.base.baseCST.OperationCS;
import org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.QVTOperationalCSPackage;
import org.eclipse.qvto.examples.xtext.qvtoperational.qvtoperationalcs.TransformationCS;

@SuppressWarnings("nls")
public class TransformationCS_AST_Part5
{
    public static final/*@NonNull*/ TransformationCS_AST_Part5 INSTANCE = new TransformationCS_AST_Part5();
    public static final /*@NonNull*/ /*@NonInvalid*/ NsURIPackageId PACKid_http_c_s_s_www_eclipse_org_s_ocl_s_3_1_0_s_BaseCST = IdManager.getNsURIPackageId("http://www.eclipse.org/ocl/3.1.0/BaseCST", BaseCSTPackage.eINSTANCE);
    public static final /*@NonNull*/ /*@NonInvalid*/ NsURIPackageId PACKid_http_c_s_s_www_eclipse_org_s_qvt_s_pivot_s_1_0_s_QVTOperationalCS = IdManager.getNsURIPackageId("http://www.eclipse.org/qvt/pivot/1.0/QVTOperationalCS", QVTOperationalCSPackage.eINSTANCE);
    public static final /*@NonNull*/ /*@NonInvalid*/ ClassId CLSSid_OperationCS = PACKid_http_c_s_s_www_eclipse_org_s_ocl_s_3_1_0_s_BaseCST.getClassId("OperationCS", 0);
    public static final /*@NonNull*/ /*@NonInvalid*/ ClassId CLSSid_TransformationCS = PACKid_http_c_s_s_www_eclipse_org_s_qvt_s_pivot_s_1_0_s_QVTOperationalCS.getClassId("TransformationCS", 0);
    public static final /*@NonNull*/ /*@NonInvalid*/ CollectionTypeId ORD_CLSSid_OperationCS = TypeId.ORDERED_SET.getSpecializedId(CLSSid_OperationCS);
    
    public /*@Nullable*/ /*@NonInvalid*/ List<OperationCS> evaluate(final /*@NonNull*/ /*@NonInvalid*/ DomainEvaluator evaluator, final /*@NonNull*/ /*@NonInvalid*/ TypeId typeId, final /*@NonNull*/ /*@NonInvalid*/ TransformationCS self) {
        final /*@NonNull*/ /*@Thrown*/ List<OperationCS> ownedOperation = self.getOwnedOperation();
        return ownedOperation;
    }
}