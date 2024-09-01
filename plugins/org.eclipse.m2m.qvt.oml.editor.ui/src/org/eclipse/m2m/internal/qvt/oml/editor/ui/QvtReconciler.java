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
		@Override
		public void partActivated(final IWorkbenchPart part) {
			if (part == myEditor) {
				QvtReconciler.this.forceReconciling();
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
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
	protected void initialProcess() {
		// processingDoneMonitor should be null, but if there is no test code to call waitTillProcessingDone
		// and if the editor reuses the reconciler then we replace the old monitor by another one regardless.
		//	assert processingDoneMonitor == null;
		processingDoneMonitor = new Object();
		super.initialProcess();
	}

	@Override
	protected void process(DirtyRegion dirtyRegion) {
		assert processingDoneMonitor != null;
	//	if (processingDoneMonitor == null) {
	//		throw new IllegalThreadStateException();
	//	}
		try {
			super.process(dirtyRegion);
		}
		finally {
			if (processingDoneMonitor != null) {
				synchronized(processingDoneMonitor) {
					processingDoneMonitor.notify();
				}
			}
		}
	}

	public void waitTillProcessingDone() throws InterruptedException {
		for (int i = 100; i <= 1000; i += 100) {		// If caller too quick wait for reconciler to be started
			if (processingDoneMonitor != null) {
				break;
			}
			Thread.sleep(i);		// 100, then 200, then 300, then 400 ms
		}
		synchronized(processingDoneMonitor) {
			processingDoneMonitor.wait();
			processingDoneMonitor = null;
		}
	}

	private final ITextEditor myEditor;
	private PartListener myPartListener;
	/*
	 * Monitor to communicate state to CompletionTest code
	 *
	 * null when idle
	 * non-null once initialProcess() has started
	 * notifies once process() completes
	 * reset to null by waitTillProcessingDone()
	 */
	private Object processingDoneMonitor = null;
}
