/*******************************************************************************
 * Copyright (c) 2007, 2018 Borland Software Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Borland Software Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.qvt.oml.editor.ui.hovers;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

/**
 * @author vrepeshko
 */
public interface IElementInfoProvider {
	Object getElementInfo(Object element, ITextViewer textViewer, IRegion region);

	/**
	 * @return the priority of this info provider by which the info providers get
	 *         sorted. A higher int is a higher priority
	 */
	default int getPriority() {
		return 0;
	}

	/**
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	default IInformationControlCreator getHoverControlCreator() {
		return parent -> new DefaultInformationControl(parent, (String) null);
	}
}
