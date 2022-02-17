package org.eclipse.m2m.tests.qvt.oml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2m.internal.qvt.oml.compiler.CompiledUnit;
import org.eclipse.m2m.internal.qvt.oml.compiler.ExeXMISerializer;
import org.eclipse.m2m.internal.qvt.oml.compiler.QVTOCompiler;
import org.eclipse.m2m.internal.qvt.oml.compiler.QvtCompilerOptions;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitProxy;
import org.eclipse.m2m.internal.qvt.oml.compiler.UnitResolverFactory;
import org.eclipse.m2m.tests.qvt.oml.ParserTests.TestData;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class Bug578265 extends TestQvtParser {
		
	public Bug578265() {
		super(TestData.createSourceChecked("bug578265", 0, 0)); //$NON-NLS-1$
	}
	
	@Override
	protected void prepareJava() throws CoreException {
				
		super.prepareJava();
		
		TestProject testProject = getTestProject();
		
		IPath destPath = new Path(getDestinationFolder().getPath());

		IWorkspace workspace = testProject.getProject().getWorkspace();
		IPath workspacePath = workspace.getRoot().getLocation();

		destPath = destPath.makeRelativeTo(workspacePath).makeAbsolute();
				
		IPath srcPath = destPath.append("src"); //$NON-NLS-1$
		
		IJavaProject javaProject = JavaCore.create(testProject.getProject());
				
		List<IClasspathEntry> classpath = new ArrayList<IClasspathEntry>(Arrays.asList(javaProject.getRawClasspath()));
				
		if (workspace.getRoot().exists(srcPath)) {		
			IClasspathAttribute testAttribute = JavaCore.newClasspathAttribute(IClasspathAttribute.TEST, Boolean.toString(true));
			classpath.add(JavaCore.newSourceEntry(srcPath, new IPath[] {}, new IPath[] {}, null, new IClasspathAttribute[] {testAttribute}));
		}
		
		IClasspathEntry[] entries = classpath.toArray(new IClasspathEntry[classpath.size()]);
		
		javaProject.setRawClasspath(entries, new NullProgressMonitor());
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		TestProject testProject = getTestProject();
		
		testProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
		
	    IMarker[] markers = testProject.getProject().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
	    for (IMarker marker : markers) {
	    	String message = marker.getAttribute(IMarker.MESSAGE).toString();
	    	Integer severity = (Integer) marker.getAttribute(IMarker.SEVERITY);
	    	assertTrue(message, severity < IMarker.SEVERITY_ERROR);
	    }
	}
		
	@Override
	public void runTest() throws Exception {
				
		super.runTest();
				
		CompiledUnit[] compiledUnits = getCompiledResults();
		ExeXMISerializer.saveUnitXMI(compiledUnits, EPackage.Registry.INSTANCE);
		
		QVTOCompiler compiler = new QVTOCompiler();
		compiler.setUseCompiledXMI(true);
		
		for(CompiledUnit unit : compiledUnits) {
			URI xmiUri = ExeXMISerializer.toXMIUnitURI(unit.getURI());
			UnitProxy proxy = UnitResolverFactory.Registry.INSTANCE.getUnit(xmiUri);
			compiler.compile(new UnitProxy[] {proxy}, new QvtCompilerOptions(), new NullProgressMonitor());
			
			Resource r = compiler.getResourceSet().getResource(xmiUri, true);
			EcoreUtil.resolveAll(r);
			assertTrue(r.getErrors().isEmpty());
		}		
	}
}
