package org.eclipse.m2m.internal.tests.qvt.oml.debugger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOThread;
import org.junit.Test;

public class VariableTests extends DebuggerTest {

	@Test
	public void testNoInternalVariablesShown() throws CoreException, InterruptedException {
		testWithModel(TestModel.SIMPLE_UML_TO_RDB) //
				.withBreakPoints(41) //
				.check(event -> {
					if (event.getKind() == DebugEvent.SUSPEND && event.getSource() instanceof QVTOThread thread) {
						thread.stepInto();

						var stackFrame = thread.getStackFrames()[0];
						var variables = stackFrame.getVariables();
						for (var variable : variables) {
							assertFalse(variable.getName().contains("__"));
							assertFalse(variable.getName().contains("_intermediate"));
						}
					}
				});
	}

	@Test
	public void testImportedProperties() throws CoreException, InterruptedException {
		testWithModel(TestModel.LIBRARY_WITH_PROPERTIES) //
				.withBreakPoints(10) //
				.check(event -> {
					if (event.getKind() == DebugEvent.SUSPEND && event.getSource() instanceof QVTOThread thread) {
						var stackFrame = thread.getStackFrames()[0];
						var variables = stackFrame.getVariables();

						List<IVariable> properties = new ArrayList<>();
						for (var variable : variables) {
							if (variable.getName().equals("this")) {
								var value = variable.getValue();
								properties.addAll(Arrays.asList(value.getVariables()));
							}
						}

						thread.resume();

						assertTrue(properties.stream().anyMatch(p -> {
							try {
								return p.getName().equals("myProperty");
							} catch (DebugException e) {
								fail("Failed to get property name");
								return false;
							}
						}));
						assertTrue(properties.stream().anyMatch(p -> {
							try {
								return p.getName().equals("myProperty2");
							} catch (DebugException e) {
								fail("Failed to get property name");
								return false;
							}
						}));
					}
				});
	}

	@Test
	public void testHasVariablesCorrect() throws CoreException, InterruptedException {
		testWithModel(TestModel.SIMPLE_UML_TO_RDB) //
				.withBreakPoints(41) //
				.check(event -> {
					if (event.getKind() == DebugEvent.SUSPEND && event.getSource() instanceof QVTOThread thread) {
						var stackFrame = thread.getStackFrames()[0];

						Throwable throwable = null;

						try {
							var variables = stackFrame.getVariables();
							for (var variable : variables) {
								checkImpliationRecursive(variable);
							}
						} catch (Throwable e) {
							throwable = e;
						}

						thread.stepInto();
						if (throwable != null) {
							throw new AssertionError(throwable);
						}
					}
				});
	}

	private void checkImpliationRecursive(IVariable variable) throws DebugException {
		var value = variable.getValue();
		var variables = value.getVariables();
		// hasVariables() => getVariables().length > 0
		assertTrue(!value.hasVariables() || variables.length > 0);

		for (var v : variables) {
			if (v.getName().equals("owner")) {
				// prevent loops
				continue;
			}
			checkImpliationRecursive(v);
		}
	}
}
