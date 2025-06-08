/*******************************************************************************
 * Copyright (c) 2022 Christopher Gerking and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.tests.qvt.oml.transform;

import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.m2m.internal.qvt.oml.project.nature.NatureUtils;
import org.eclipse.m2m.tests.qvt.oml.TestProject;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.natures.PluginProject;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModelBase;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.junit.Assert;

@SuppressWarnings("restriction")
public class PluginDependencyProjectData extends ReferencedProjectData {
	
	public PluginDependencyProjectData(String myName, String referencedName, boolean useCycleReferences) {
		super(myName, referencedName, useCycleReferences);
	}
	
	private WorkspacePluginModelBase myPluginModel;
	private WorkspacePluginModelBase referencedPluginModel;
	
	@Override
	public void prepare(TestProject project) throws Exception {		
		super.prepare(project);
		
		IProject myProject = project.getProject();
		NatureUtils.addNature(myProject, IBundleProjectDescription.PLUGIN_NATURE);
				
		IFile myPluginXml = PDEProject.getPluginXml(myProject);
		IFile myManifest = PDEProject.getManifest(myProject);					
		myPluginModel = new WorkspaceBundlePluginModel(myManifest, myPluginXml);
		IPluginBase myPluginBase = myPluginModel.getPluginBase();
		myPluginBase.setId(myProject.getName());
		
		IProject[] referencedProjects = myProject.getReferencedProjects();
		
		IProjectDescription desc = myProject.getDescription();
		desc.setReferencedProjects(new IProject[] {});
		myProject.setDescription(desc, null);
		Assert.assertEquals(myProject.getReferencedProjects().length, 0);
		
		for (IProject referencedProject : referencedProjects) {
							
			NatureUtils.addNature(referencedProject, IBundleProjectDescription.PLUGIN_NATURE);
			
			IFile referencedPluginXml = PDEProject.getPluginXml(referencedProject);
			IFile referencedManifest = PDEProject.getManifest(referencedProject);					
			referencedPluginModel = new WorkspaceBundlePluginModel(referencedManifest, referencedPluginXml);
			IPluginBase referencedPluginBase = referencedPluginModel.getPluginBase();
			referencedPluginBase.setId(referencedProject.getName());
			referencedPluginModel.save();
			
			IPluginImport pluginImport = myPluginModel.createImport(referencedPluginBase.getId());
			IPluginBase pluginBase = myPluginModel.getPluginBase();
			pluginBase.add(pluginImport);
			
			if (isUseCycleReferences) {				
				IPluginImport cyclicImport = referencedPluginModel.createImport(myPluginBase.getId());
				referencedPluginBase.add(cyclicImport);
				referencedPluginModel.save();
			}
		}
				
		myPluginModel.save();
	}
		
	@Override
	public void dispose(TestProject testProject) throws Exception {
		myPluginModel.dispose();
		referencedPluginModel.dispose();
		
		IProject project = testProject.getProject();
		IProjectDescription desc = project.getDescription();
		
		NatureUtils.removeNature(desc, IBundleProjectDescription.PLUGIN_NATURE);
		project.setDescription(desc, null);
		
		ICommand manifestCommand = NatureUtils.findCommand(project, PluginProject.MANIFEST_BUILDER_ID);
		ICommand schemaCommand = NatureUtils.findCommand(project, PluginProject.SCHEMA_BUILDER_ID);
		assertNull(manifestCommand);
		assertNull(schemaCommand);
				
		IFile myPluginXml = PDEProject.getPluginXml(project);
		IFile myManifest = PDEProject.getManifest(project);		
		
		myPluginXml.delete(true, null);
		myManifest.delete(true, null);
		
		super.dispose(testProject);
	}
}