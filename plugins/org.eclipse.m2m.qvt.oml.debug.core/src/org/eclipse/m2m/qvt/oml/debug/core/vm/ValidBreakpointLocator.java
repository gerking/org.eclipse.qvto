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
package org.eclipse.m2m.qvt.oml.debug.core.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalAstWalker;
import org.eclipse.m2m.internal.qvt.oml.common.util.LineNumberProvider;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.expressions.ContextualProperty;
import org.eclipse.m2m.internal.qvt.oml.expressions.ExpressionsPackage;
import org.eclipse.m2m.internal.qvt.oml.expressions.ImperativeOperation;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingBody;
import org.eclipse.m2m.internal.qvt.oml.expressions.MappingCallExp;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;
import org.eclipse.m2m.internal.qvt.oml.expressions.ObjectExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.BlockExp;
import org.eclipse.m2m.qvt.oml.ecore.ImperativeOCL.ImperativeLoopExp;
import org.eclipse.ocl.ecore.IteratorExp;
import org.eclipse.ocl.ecore.OperationCallExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.utilities.ASTNode;
import org.eclipse.ocl.utilities.Visitable;

public class ValidBreakpointLocator {
	
	private static final class BreakpointableNodeLocator implements QvtOperationalAstWalker.NodeProcessor {
		private final List<ASTNode> elements;
		private final int lineNumber;
		private final LineNumberProvider lineNumbers;

		BreakpointableNodeLocator(int lineNumber, LineNumberProvider lineNumbers) {
			this.elements = new ArrayList<ASTNode>();
			this.lineNumber = lineNumber;
			this.lineNumbers = lineNumbers;
		}

		public void process(Visitable visitable, Visitable parent) {
			if (!(visitable instanceof ASTNode astNode)) {
				return;
			}
			
			boolean found = false;
			if(isBreakpointableElementStart(astNode)) {
		        int line = lineNumbers.getLineNumber(astNode.getStartPosition());
				if(line == lineNumber) {
					elements.add(astNode);
					found = true;
				}
			}
			
			if(!found && isBreakpointableElementEnd(astNode)) {
		        int line = lineNumbers.getLineNumber(astNode.getEndPosition());
				if(line == lineNumber) {
					elements.add(astNode);
				}
			}	
		}
	}
	

	private ValidBreakpointLocator() {
		super();
	}


	public static List<ASTNode> getBreakpointableElementsForLine(CompiledUnit compiledModule, final LineNumberProvider lineNumbers, final int lineNumber) {
		BreakpointableNodeLocator locator = new BreakpointableNodeLocator(lineNumber, lineNumbers);
		QvtOperationalAstWalker walker = new QvtOperationalAstWalker(locator);
		
		for(Module nextModule : compiledModule.getModules()) {
			nextModule.accept(walker);

			List<ASTNode> elements = locator.elements;
			if(!elements.isEmpty()) {
				// already found, can't be spread across multiple modules
				return elements;
			}
		}
		
		return Collections.emptyList();
	}
	

	static boolean isBreakpointableElementStart(ASTNode element) {
		boolean breakpointable = element instanceof OCLExpression<?> && !(element instanceof BlockExp) && !(element instanceof IteratorExp)
				&& !(element instanceof ContextualProperty) && !(element instanceof ImperativeLoopExp);

        if (breakpointable && (element instanceof ObjectExp)) {
        	if(element.eContainer() instanceof MappingBody mappingBody &&
        		element.eContainingFeature() == ExpressionsPackage.eINSTANCE.getOperationBody_Content()) {
        		// Remark:
        		// Here we try to check if the object expression is implicit
        		// IOW, has no concrete syntaxt element => mapped to mapping
        		// body by its positions
                ObjectExp objectExp = (ObjectExp) element;
        		return mappingBody.getStartPosition() != objectExp.getStartPosition();
        	}
        }
		
		return breakpointable;		
	}
	
	static boolean isBreakpointableElementEnd(ASTNode element) {
		EOperation referredOperation = null;
		if (element instanceof OperationCallExp) {
			referredOperation = ((OperationCallExp) element).getReferredOperation();
		}

        return element instanceof ImperativeOperation
                || element instanceof MappingCallExp
                || referredOperation instanceof ImperativeOperation;
	}
}
