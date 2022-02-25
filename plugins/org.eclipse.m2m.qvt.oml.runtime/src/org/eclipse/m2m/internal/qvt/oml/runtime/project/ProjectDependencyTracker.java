/*******************************************************************************
 * Copyright (c) 2016, 2021 Christopher Gerking and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.runtime.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.m2m.internal.qvt.oml.runtime.QvtRuntimePlugin;

public abstract class ProjectDependencyTracker {
	
	public static final String POINT = QvtRuntimePlugin.ID + ".qvtProjectDependencyTracker";
	
	private static Collection<ProjectDependencyTracker> trackers = null;
	
	private static Collection<ProjectDependencyTracker> getTrackers() {
		if (trackers == null) {
			
			IConfigurationElement[] providers = Platform.getExtensionRegistry().getConfigurationElementsFor(POINT);
			trackers = new ArrayList<ProjectDependencyTracker>(providers.length);
			
			for (IConfigurationElement provider : providers) {
				try {
			        Object extension = provider.createExecutableExtension("class");
			        if (extension instanceof ProjectDependencyTracker) {
			        	trackers.add((ProjectDependencyTracker) extension);
			        }
				}
				catch(CoreException e) {
					QvtRuntimePlugin.log(e.getStatus());
				}
			}
		}
		
		return trackers;
	}
	
	public static Set<IProject> getAllReferencedProjects(IProject project, boolean recursive) {
		
		IConfigurationElement[] providers = Platform.getExtensionRegistry().getConfigurationElementsFor(POINT);
		
		Set<IProject> referencedProjects = new HashSet<IProject>(providers.length);
		
		for (ProjectDependencyTracker tracker : getTrackers()) {
			referencedProjects.addAll(tracker.getReferencedProjects(project, recursive));
	    }
		
		return referencedProjects;		
	}
	
	public abstract Set<IProject> getReferencedProjects(IProject project, boolean recursive);

}
