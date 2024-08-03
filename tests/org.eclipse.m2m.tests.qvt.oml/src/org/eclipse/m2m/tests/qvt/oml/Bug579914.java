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
package org.eclipse.m2m.tests.qvt.oml;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2m.internal.qvt.oml.QvtPlugin;
import org.eclipse.m2m.internal.qvt.oml.common.io.FileUtil;
import org.eclipse.m2m.tests.qvt.oml.ParserTests.TestData;
import org.eclipse.m2m.tests.qvt.oml.util.TestUtil;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Bug579914 extends TestQvtParser {
	
	private IProject qvtPluginProject;
	
	public Bug579914() {
		super(TestData.createSourceChecked("bug579914", 0, 0)); //$NON-NLS-1$
	}
		
	@Override
	public void setUp() throws Exception {
		
		TestUtil.turnOffAutoBuildingAndJoinBuildJobs();
		
		IPluginModelBase qvtPluginModel = PluginRegistry.findModel(QvtPlugin.ID);
		String installLocation = qvtPluginModel.getInstallLocation();
				
		File sourceFolder = new File(installLocation);
				
		if (!sourceFolder.isDirectory()) {
			IPath currentPath = new Path(new File(".").getAbsolutePath()); //$NON-NLS-1$
			
			List<String> segments = Arrays.asList(currentPath.segments());
			int indexOfTestsBundle = segments.indexOf(AllTests.BUNDLE_ID);
			
			IPath qvtPath = currentPath.uptoSegment(indexOfTestsBundle).removeLastSegments(1).append("plugins").append(QvtPlugin.ID); //$NON-NLS-1$
						
			sourceFolder = new File(qvtPath.toString());
			assertTrue(sourceFolder.isDirectory());
		}
		
		qvtPluginProject = ResourcesPlugin.getWorkspace().getRoot().getProject(QvtPlugin.ID);		
		qvtPluginProject.create(null);
		qvtPluginProject.open(null);
		
		IPath targetPath = qvtPluginProject.getLocation();
		File targetFolder = new File(targetPath.toString());
		targetFolder.mkdirs();
		assertTrue(targetFolder.isDirectory());
				
		FileUtil.copyFolder(sourceFolder, targetFolder);
		qvtPluginProject.refreshLocal(IResource.DEPTH_INFINITE, null);

		// restrict natures to Java and Plugin (bug 582831)
		IProjectDescription projectDescription = qvtPluginProject.getDescription();
		String[] natureIDs = {JavaCore.NATURE_ID, IBundleProjectDescription.PLUGIN_NATURE};	
		projectDescription.setNatureIds(natureIDs);
		qvtPluginProject.setDescription(projectDescription, null);
		assertArrayEquals(natureIDs, qvtPluginProject.getDescription().getNatureIds());
		
		assertTrue(TestUtil.getBuildErrors(qvtPluginProject).isEmpty());
		TestUtil.buildProject(qvtPluginProject);
		
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		
		if (qvtPluginProject != null && qvtPluginProject.exists()) {
			qvtPluginProject.delete(true, null);
		}
	}
}
