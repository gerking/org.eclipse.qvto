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
package org.eclipse.m2m.qvt.oml.debug.core.launch;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.m2m.internal.qvt.oml.common.launch.StreamsProxy;
import org.eclipse.m2m.internal.qvt.oml.common.launch.TargetUriData;
import org.eclipse.m2m.internal.qvt.oml.runtime.launch.QvtLaunchConfigurationDelegate;
import org.eclipse.m2m.internal.qvt.oml.runtime.launch.QvtLaunchUtil;
import org.eclipse.m2m.qvt.oml.ExecutionContext;
import org.eclipse.m2m.qvt.oml.debug.core.QVTODebugCore;
import org.eclipse.m2m.qvt.oml.debug.core.QVTODebugTarget;
import org.eclipse.m2m.qvt.oml.debug.core.QVTODebugUtil;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOVirtualProcess;
import org.eclipse.m2m.qvt.oml.debug.core.app.DebugRunnerFactory;
import org.eclipse.m2m.qvt.oml.debug.core.app.DebugTransformationRunner;
import org.eclipse.m2m.qvt.oml.debug.core.vm.DebuggableExecutorAdapter;
import org.eclipse.m2m.qvt.oml.debug.core.vm.IQVTOVirtualMachineShell;
import org.eclipse.m2m.qvt.oml.debug.core.vm.QVTOVirtualMachine;
import org.eclipse.m2m.qvt.oml.util.WriterLog;

public class QVTODebugConfiguration extends QvtLaunchConfigurationDelegate {
	
	public static final IStatus MODIFIED_SOURCE_STATUS = QVTODebugCore.createError("", 300, null); //$NON-NLS-1$
	
	public QVTODebugConfiguration() {
		super();
	}
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		StreamsProxy streamsProxy = new StreamsProxy();
		ExecutionContext context = QvtLaunchUtil.createContext(configuration, new WriterLog(streamsProxy.getOutputWriter(), true), monitor);
				
		DebugTransformationRunner runner = createRunner(configuration);
		runner.setErrorLog(new PrintWriter(streamsProxy.getErrWriter(), true));
		
		Diagnostic initDiagnostic = runner.initialize();
		if(initDiagnostic.getSeverity() == Diagnostic.ERROR) {
			throw new CoreException(BasicDiagnostic.toIStatus(initDiagnostic));			
		}
		
		if (QvtLaunchUtil.shouldGenerateTraceFile(configuration)
				|| QvtLaunchUtil.isIncrementalUpdate(configuration)) {
			IStatus showTraceViewStatus = QVTODebugCore.createError("", 400, null); //$NON-NLS-1$

			IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(showTraceViewStatus);
			if(handler != null) {
				try {
					handler.handleStatus(showTraceViewStatus, null);
				} catch (CoreException e) {
					QVTODebugCore.log(e.getStatus());
				}
			}
		}
		
		DebuggableExecutorAdapter executable = runner.createDebuggableAdapter(context);
		IQVTOVirtualMachineShell vm = new QVTOVirtualMachine(executable);
		
		QVTOVirtualProcess process = new QVTOVirtualProcess(launch, vm);
		process.setStreamsProxy(streamsProxy);
		
		List<IFile> transformationWsFile = QVTODebugUtil.toFiles(runner.getTransformationURI());
		if(!transformationWsFile.isEmpty()) {			
			addSourceModificationListener(transformationWsFile.get(0), process);
		}
		
		QVTODebugTarget debugTarget = new QVTODebugTarget(process, vm);		
		launch.addDebugTarget(debugTarget);
	}
		
	private DebugTransformationRunner createRunner(ILaunchConfiguration configuration) throws CoreException {
		DebugRunnerFactory runnerFactory = new DebugRunnerFactory();

        runnerFactory.transformationURI = QvtLaunchUtil.getTransformationURI(configuration);
		
		List<String> modelURIs = new ArrayList<String>();
		for (TargetUriData uriData : QvtLaunchUtil.getTargetUris(configuration)) {
			modelURIs.add(uriData.getUriString());
		}
		
		runnerFactory.modelParamURI = modelURIs;

		runnerFactory.isSaveTrace = QvtLaunchUtil.shouldGenerateTraceFile(configuration);
		runnerFactory.isIncrementalUpdate = QvtLaunchUtil.isIncrementalUpdate(configuration);
		String traceFileURI = QvtLaunchUtil.getTraceFileURI(configuration);
		if (traceFileURI != null && !traceFileURI.trim().isEmpty()) {
			runnerFactory.traceFileURI = traceFileURI;
		}
		
		try {
			return runnerFactory.createRunner();
		} catch(DiagnosticException e) {
			throw new CoreException(BasicDiagnostic.toIStatus(e.getDiagnostic()));
		}
	}
	
	/*
	 * TODO - handle multiple files involved in the transformation
	 */
	private void addSourceModificationListener(final IFile unitFile, final ITerminate terminate) {
		final SourceModificationListener listener = new SourceModificationListener(unitFile, terminate);

		unitFile.getProject().getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);

		DebugPlugin.getDefault().addDebugEventListener(
				new IDebugEventSetListener() {
					public void handleDebugEvents(DebugEvent[] events) {
                        for (DebugEvent event : events) {
                            if (event.getKind() == DebugEvent.TERMINATE && event.getSource().equals(terminate)) {
                                // unregister myself
                                DebugPlugin.getDefault().removeDebugEventListener(this);
                                // unregister workspace listener
                                unitFile.getProject().getWorkspace().removeResourceChangeListener(listener);
                            }
                        }
					}
				});
	
	}

}