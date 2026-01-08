/*******************************************************************************
 * Copyright (c) 2009, 2018 R.Dvorak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Radek Dvorak - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.debug.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * Creates adapters for retargettable actions in debug platform.
 * Contributed via <code>org.eclipse.core.runtime.adapters</code> 
 * extension point. 
 */
public class RetargettableActionAdapterFactory implements IAdapterFactory {
	
	public RetargettableActionAdapterFactory() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
        if (!(adaptableObject instanceof ITextEditor editorPart)) {
			return null;
        }
		
        IResource resource = editorPart.getEditorInput().getAdapter(IResource.class);
        if (resource != null) {
            String editorID = editorPart.getEditorSite().getId();
			if(!QVTODebugUIPlugin.DEBUG_EDITOR_ID.equals(editorID)) {
				return null;
            }
        }

        if(IRunToLineTarget.class == adapterType) {
			return (T) new QVTORunToLineAdapter();
        } else  if(IToggleBreakpointsTarget.class == adapterType) {
			return (T) new QVTOToggleBreakpointAdapter();
        } 

		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class[] { QVTOToggleBreakpointAdapter.class, QVTORunToLineAdapter.class };
	}
}
