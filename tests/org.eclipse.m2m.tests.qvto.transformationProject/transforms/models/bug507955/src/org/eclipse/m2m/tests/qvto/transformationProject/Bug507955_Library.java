/*******************************************************************************
 * Copyright (c) 2022, 2023 Christopher Gerking and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.tests.qvto.transformationProject;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.m2m.qvt.oml.blackbox.java.Module;

@Module(packageURIs={"http://www.eclipse.org/emf/2002/Ecore"})
public class Bug507955_Library {
	
	public Bug507955_Library() {}
	
	public EClass mirrorBug507955(EClass c) {
		return c;
	}

}
