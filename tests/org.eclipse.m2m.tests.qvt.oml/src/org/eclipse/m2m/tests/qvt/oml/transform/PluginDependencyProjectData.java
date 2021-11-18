package org.eclipse.m2m.tests.qvt.oml.transform;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2m.tests.qvt.oml.TestProject;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.internal.core.bundle.WorkspaceBundlePluginModel;
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModelBase;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.pde.internal.core.util.CoreUtility;

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
		CoreUtility.addNatureToProject(myProject, IBundleProjectDescription.PLUGIN_NATURE, new NullProgressMonitor());
				
		IFile myPluginXml = PDEProject.getPluginXml(myProject);
		IFile myManifest = PDEProject.getManifest(myProject);					
		myPluginModel = new WorkspaceBundlePluginModel(myManifest, myPluginXml);
		IPluginBase myPluginBase = myPluginModel.getPluginBase();
		myPluginBase.setId(myProject.getName());
		
		IProject[] referencedProjects = myProject.getReferencedProjects();
		for (IProject referencedProject : referencedProjects) {
							
			CoreUtility.addNatureToProject(referencedProject, IBundleProjectDescription.PLUGIN_NATURE, new NullProgressMonitor());
			
			IFile referencedPluginXml = PDEProject.getPluginXml(referencedProject);
			IFile referencedManifest = PDEProject.getManifest(referencedProject);					
			referencedPluginModel = new WorkspaceBundlePluginModel(referencedManifest, referencedPluginXml);
			IPluginBase referencedPluginBase = referencedPluginModel.getPluginBase();
			referencedPluginBase.setId(referencedProject.getName());
			IProject[] referencedReferencedProjects = referencedProject.getReferencedProjects();
			for (IProject referencedReferencedProject : referencedReferencedProjects) {
				IPluginImport pluginImport;
				if (referencedReferencedProject == myProject) {
					pluginImport = myPluginModel.createImport(myPluginBase.getId());
				}
				else {
					IFile referencedReferencedPluginXml = PDEProject.getPluginXml(referencedReferencedProject);
					IFile referencedReferencedManifest = PDEProject.getManifest(referencedReferencedProject);					
					WorkspacePluginModelBase referencedReferencedPluginModel = new WorkspaceBundlePluginModel(referencedReferencedManifest, referencedReferencedPluginXml);
					IPluginBase referencedReferencedPluginBase = referencedReferencedPluginModel.getPluginBase();
					referencedReferencedPluginBase.setId(referencedReferencedProject.getName());
					pluginImport = referencedReferencedPluginModel.createImport(referencedReferencedPluginBase.getId());
				}
				referencedPluginBase.add(pluginImport);
			}
			referencedPluginModel.save();
			
			IPluginImport pluginImport = referencedPluginModel.createImport(referencedPluginBase.getId());
			myPluginBase.add(pluginImport);
		}
				
		myPluginModel.save();
	}
		
	@Override
	public void dispose(TestProject project) throws Exception {
		myPluginModel.dispose();
		referencedPluginModel.dispose();
		
		IFile myPluginXml = PDEProject.getPluginXml(project.getProject());
		IFile myManifest = PDEProject.getManifest(project.getProject());		
		
		myPluginXml.delete(true, null);
		myManifest.delete(true, null);
		
		super.dispose(project);
	}
}
