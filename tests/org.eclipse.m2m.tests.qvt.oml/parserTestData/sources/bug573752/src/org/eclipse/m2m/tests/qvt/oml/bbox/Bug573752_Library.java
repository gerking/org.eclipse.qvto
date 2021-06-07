/*******************************************************************************
 * Copyright (c) 2021 Christopher Gerking and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Christopher Gerking - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.tests.qvt.oml.bbox;

import org.eclipse.m2m.qvt.oml.blackbox.java.Module;

import testqvt.BooleanElement;
import testqvt.Numbers;
import testqvt.TestqvtFactory;

@Module(packageURIs={"http://www.eclipse.org/m2m/qvt/oml/testqvt"})
public class Bug573752_Library {
	
	public BooleanElement test(Numbers numbers) {
		return TestqvtFactory.eINSTANCE.createBooleanElement();
	}

}
