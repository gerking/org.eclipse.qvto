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
 *     Christopher Gerking - bug 391289
 *******************************************************************************/
package org.eclipse.m2m.internal.qvt.oml.editor.ui;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.compiler.QvtCompilerOptions;
import org.eclipse.ui.texteditor.ITextEditor;


public class QvtReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
    private static final int MAX_LOGGED_COMPILATION_EXCEPTIONS = 5;
    
	private IProgressMonitor myMonitor;
	private IDocument myDocument;
	private IQVTReconcilingListener myReconcilingListener;	
	private final ITextEditor myEditor;
	private int myLoggedCompilationExceptionsCount = 0;
	private CompletableFuture<CompiledUnit> myCompilation = new CompletableFuture<CompiledUnit>();

    public QvtReconcilingStrategy(final ITextEditor editor) {
        myEditor = editor;
        if(editor instanceof IQVTReconcilingListener) {
			myReconcilingListener = (IQVTReconcilingListener) editor;
        }
    }
    
    public void setDocument(final IDocument document) {
        myDocument = document;
    }
    
    public void reconcile(final DirtyRegion dirtyRegion, final IRegion subRegion) {
        reconcileInternal();
    }
    
    public void reconcile(final IRegion partition) {
        reconcileInternal();
    }
    
    public void setProgressMonitor(final IProgressMonitor monitor) {
        myMonitor = monitor;
    }
    
    public void initialReconcile() {
        reconcileInternal();
    }
    
    private void reconcileInternal() {
        boolean editingInQvtSourceContainer = QvtCompilerFacade.isEditingInQvtSourceContainer(myEditor);
        if(!editingInQvtSourceContainer) {
        	return;
        }
                                
        CompiledUnit compilationResult = null;
        try {
			myReconcilingListener.aboutToBeReconciled();			
	        compilationResult = getCompilationResult(editingInQvtSourceContainer);	        
        } 
        catch (Exception ex) {
            handleError(ex);
        } finally {
        	myReconcilingListener.reconciled(compilationResult, myMonitor);
        }
    }

	private CompiledUnit getCompilationResult(boolean editingInQvtSourceContainer) {
		QvtCompilerOptions options = new QvtCompilerOptions();
		options.setShowAnnotations(editingInQvtSourceContainer);
		options.setSourceLineNumbersEnabled(false);
		options.enableCSTModelToken(true);
		
		CompiledUnit compilationResult = QvtCompilerFacade.getInstance().compile(myEditor, myDocument, options, myMonitor);
		myCompilation.complete(compilationResult);		
		return compilationResult;
	}

	private void handleError(Exception ex) {
		
		if (ex instanceof OperationCanceledException) {
			return;
		}
		
		if (myLoggedCompilationExceptionsCount < MAX_LOGGED_COMPILATION_EXCEPTIONS) {
		    myLoggedCompilationExceptionsCount ++;
		    Activator.log(ex);
		    if (myLoggedCompilationExceptionsCount == MAX_LOGGED_COMPILATION_EXCEPTIONS) {
		        Activator.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, Messages.QvtReconcilingStrategy_TooManyExceptions));
		    }
		}
	}
	
	public void waitTillProcessingDone() {
		myCompilation.join();
	}
}