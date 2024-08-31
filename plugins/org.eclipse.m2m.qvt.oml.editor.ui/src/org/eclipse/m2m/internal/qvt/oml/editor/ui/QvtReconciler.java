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
package org.eclipse.m2m.internal.qvt.oml.editor.ui;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

public class QvtReconciler extends MonoReconciler {

	private class PartListener implements IPartListener {
		public void partActivated(final IWorkbenchPart part) {
			if (part == myEditor) {
				QvtReconciler.this.forceReconciling();
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	}
	
	public QvtReconciler(final ITextEditor textEditor,
			final IReconcilingStrategy strategy, final boolean isIncremental) {
		super(strategy, isIncremental);
		myEditor = textEditor;
	}
	
	void doForceReconciling() {
		super.forceReconciling();
	}
	
	@Override
	public void install(final ITextViewer textViewer) {
		super.install(textViewer);

		myPartListener = new PartListener();
		IWorkbenchPartSite site = myEditor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		window.getPartService().addPartListener(myPartListener);
	}

	@Override
	public void uninstall() {
		IWorkbenchPartSite site = myEditor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		window.getPartService().removePartListener(myPartListener);
		myPartListener = null;

		super.uninstall();
	}
	
	@Override
	protected void process(DirtyRegion dirtyRegion) {		
		synchronized(this) {
			if (fIsFinished) throw new IllegalThreadStateException();
			
			super.process(dirtyRegion);
            
			fIsFinished = true;
            this.notify();
       }
	}
	
	public boolean isFinished() {
		return fIsFinished;
	}
	
	
	private final ITextEditor myEditor;
	private PartListener myPartListener;
	private boolean fIsFinished = false;
}
