/*******************************************************************************
 * Copyright (c) 2016, 2018 Christopher Gerking and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.jdt.pde;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2m.internal.qvt.oml.runtime.project.ProjectDependencyTracker;
import org.eclipse.pde.core.plugin.IPluginImport;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

public class PdeDependencyTracker extends ProjectDependencyTracker {
	
	public PdeDependencyTracker() {}
	
	public Set<IProject> getReferencedProjects(IProject project, boolean recursive) {
		Set<IProject> allProjects = new HashSet<IProject>();
		getReferencedProjectsInternal(project, true, allProjects);
		allProjects.remove(project);
		return allProjects;
	}
	private void getReferencedProjectsInternal(IProject project, boolean recursive, Set<IProject> allProjects) {
		if (allProjects.add(project)) {
			IPluginModelBase plugin = findPluginModelByProject(project);
			if (plugin != null) {		
				IPluginImport[] imports = plugin.getPluginBase().getImports();
				for (IPluginImport nextImport : imports) {
					String importID = nextImport.getId();
					IPluginModelBase depPlugin = findPluginModelByID(importID);
					if (depPlugin != null && depPlugin.getUnderlyingResource() != null) {
						IProject projectDep = depPlugin.getUnderlyingResource().getProject();
						if (recursive) {
							getReferencedProjectsInternal(projectDep, true, allProjects);
						}
						else {
							allProjects.add(projectDep);
						}
					}
				}
			}
		}
	}
	
	private static IPluginModelBase findPluginModelByProject(IProject project) {
		return PluginRegistry.findModel(project);
	}

	private static IPluginModelBase findPluginModelByID(String importID) {
		return PluginRegistry.findModel(importID);
	}

}
