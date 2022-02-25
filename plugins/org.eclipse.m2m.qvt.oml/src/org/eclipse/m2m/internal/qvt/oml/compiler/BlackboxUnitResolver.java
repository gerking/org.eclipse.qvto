/*******************************************************************************
 * Copyright (c) 2009, 2022 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *     Christopher Gerking - bug 537041
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.m2m.internal.qvt.oml.NLS;
import org.eclipse.m2m.internal.qvt.oml.QvtMessage;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.ast.binding.ASTBindingHelper;
import org.eclipse.m2m.internal.qvt.oml.ast.env.QvtOperationalModuleEnv;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.QvtOperationalParserUtil;
import org.eclipse.m2m.internal.qvt.oml.ast.parser.ValidationMessages;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxException;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxRegistry;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxUnit;
import org.eclipse.m2m.internal.qvt.oml.blackbox.BlackboxUnitDescriptor;
import org.eclipse.m2m.internal.qvt.oml.blackbox.LoadContext;
import org.eclipse.m2m.internal.qvt.oml.blackbox.ResolutionContext;
import org.eclipse.m2m.internal.qvt.oml.blackbox.ResolutionContextImpl;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitContents.ModelContents;
import org.eclipse.m2m.internal.qvt.oml.emf.util.mmregistry.IMetamodelRegistryProvider;
import org.eclipse.m2m.internal.qvt.oml.expressions.Module;

public class BlackboxUnitResolver implements UnitResolver {
	
	public static final URI GLOBAL_CONTEXT = URI.createURI("/"); //$NON-NLS-1$ 

	// the global scope black-box resolver
	public static final BlackboxUnitResolver DEFAULT = new BlackboxUnitResolver(GLOBAL_CONTEXT);
	
	private ResolutionContext fContext;
	
	public BlackboxUnitResolver(URI context) {
		fContext = new ResolutionContextImpl(context);
	}
	
	public static boolean isBlackboxUnitURI(URI uri) {
		return BlackboxUnitDescriptor.URI_QVTO_SCHEME.equals(uri.scheme()) && 
			BlackboxUnitDescriptor.URI_BLACKBOX_AUTHORITY.equals(uri.authority());
	}
		
	public UnitProxy resolveUnit(String qualifiedName) {
		
		BlackboxUnitDescriptor descriptor = BlackboxRegistry.INSTANCE.getCompilationUnitDescriptor(qualifiedName, fContext);
		
		if (descriptor != null) {
			int namePos = qualifiedName.lastIndexOf('.');
			String name;
			String namespace;
			if(namePos > 0) {
				namespace = qualifiedName.substring(0, namePos);
				
				if(namePos + 1 < qualifiedName.length()) {
					++namePos;
				}
				name = qualifiedName.substring(namePos);

			} else {
				name = qualifiedName;
				namespace = null;
			}
			
			return new BlackboxUnitProxy(namespace, name, descriptor);
		}

		return null;
	}


	class BlackboxUnitProxy extends UnitProxy {

		private BlackboxUnitDescriptor fDescriptor;
		
		BlackboxUnitProxy(String namespace, String name, BlackboxUnitDescriptor descriptor) {
			super(namespace, name, descriptor.getURI());
			
			fDescriptor = descriptor;
		}
		
		@Override
		public int getContentType() {
			return UnitProxy.TYPE_MODEL;
		}
		
		@Override
		public BlackboxUnitContents getContents() {
			return new BlackboxUnitContents(fDescriptor);
		}

		@Override
		public UnitResolver getResolver() {
			return BlackboxUnitResolver.this;
		}
		
		public CompiledUnit load(IMetamodelRegistryProvider provider) {
			BlackboxUnitContents contents = getContents();
	    	
	    	List<Module> modules = contents.loadElements(CompilerUtils.getEPackageRegistry(fDescriptor.reconvertURI(), provider));
	    	List<QvtOperationalModuleEnv> modelEnvs = new ArrayList<QvtOperationalModuleEnv>(modules.size());
	    	
	    	for (Module nextModule : modules) {
	    		QvtOperationalModuleEnv nextEnv = ASTBindingHelper.getEnvironment(nextModule, QvtOperationalModuleEnv.class);
	    		if(nextEnv != null) {
	    			// FIXME -
	    			// clear the environment problems, for now we do not consider errors
	    			// like duplicate operation definitions to cause the importing unit to fail
	    			nextEnv.clearProblems();
	    			
	    			modelEnvs.add(nextEnv);
	    		}
			}
	    		    	
	    	CompiledUnit compiledUnit = new CompiledUnit(Arrays.asList(ResolverUtils.getNameSegments(getQualifiedName())), getURI(), modelEnvs, provider.getResolutionResourceSet());
	    	Diagnostic loadProblems = contents.getProblems();
			if(loadProblems != null) {
	    		compiledUnit.addProblem(new QvtMessage(loadProblems.getMessage()));
	    	}
	    	
			return compiledUnit;
		}
	}
	
	class BlackboxUnitContents implements ModelContents {
		
		private BlackboxUnitDescriptor fDescriptor;
		private Diagnostic fProblems;
		
		
		BlackboxUnitContents(BlackboxUnitDescriptor descriptor) {
			assert descriptor != null;
			fDescriptor = descriptor;
		}
				
		public Diagnostic getProblems() {
			return fProblems;
		}
		
		public List<Module> loadElements(Registry packageRegistry) {
			LoadContext loadContext = new LoadContext(packageRegistry);
			BlackboxUnit cunit = null;
			try {
				cunit = fDescriptor.load(loadContext);
				if (cunit.getDiagnostic().getSeverity() == Diagnostic.ERROR) {
					String errMessage = NLS.bind(ValidationMessages.FailedToLoadUnit, fDescriptor.getQualifiedName());
					fProblems = new BasicDiagnostic(cunit.getDiagnostic().getSource(), cunit.getDiagnostic().getCode(), 
							QvtOperationalParserUtil.wrappInSeeErrorLogMessage(errMessage), null);
				}
			} catch (BlackboxException e) {
				Diagnostic diagnostic = e.getDiagnostic();
				if(diagnostic != null) {
					QvtPlugin.logDiagnostic(diagnostic);					
				} else {
					QvtPlugin.error(NLS.bind(ValidationMessages.FailedToLoadUnit, 
							new Object[] { fDescriptor.getQualifiedName() }), e);
				}

				String errMessage = NLS.bind(ValidationMessages.FailedToLoadUnit, fDescriptor.getQualifiedName());
				fProblems = new BasicDiagnostic(diagnostic.getSource(), diagnostic.getCode(), 
						QvtOperationalParserUtil.wrappInSeeErrorLogMessage(errMessage), null);
			}
			
			if(cunit == null) {
				return Collections.emptyList();
			}
			
			List<QvtOperationalModuleEnv> elementEnvs = cunit.getElements();			
			List<Module> unitElements = new ArrayList<Module>(elementEnvs.size());
						
			for (QvtOperationalModuleEnv nextEnv : elementEnvs) {
				Module module = nextEnv.getModuleContextType();
				if(module != null) {
					unitElements.add(module);
					ASTBindingHelper.setEnvironment(module, nextEnv);					
				}
			}

			return unitElements;   
		}
	}
	
}
