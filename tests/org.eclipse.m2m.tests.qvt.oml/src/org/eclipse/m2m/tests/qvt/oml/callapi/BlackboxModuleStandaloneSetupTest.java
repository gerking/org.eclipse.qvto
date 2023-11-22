/*******************************************************************************
 * Copyright (c) 2014, 2023 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Boyko Sergey - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.tests.qvt.oml.callapi;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.m2m.qvt.oml.TransformationExecutor;
import org.eclipse.m2m.tests.qvt.oml.bbox.StandaloneBBoxModuleRegistered;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BlackboxModuleStandaloneSetupTest extends TransformationExecutorTest {
	
	public BlackboxModuleStandaloneSetupTest(String name) {
		super(name, Collections.<String>emptyList());
	}
	
	@Parameters
	public static Iterable<String> data() {
		return Arrays.asList(
			new String[] {
				"bug326871_standalone", //$NON-NLS-1$
				"bug326871a_standalone" //$NON-NLS-1$
			}
		);
    }
	
	@Override
	@Before
	public void setUp() {
		TransformationExecutor.BlackboxRegistry.INSTANCE.registerModules(StandaloneBBoxModuleRegistered.class);
		super.setUp();
	}	
}
