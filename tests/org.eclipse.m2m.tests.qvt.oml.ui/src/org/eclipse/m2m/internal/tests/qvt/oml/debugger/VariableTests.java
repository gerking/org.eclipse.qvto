package org.eclipse.m2m.internal.tests.qvt.oml.debugger;

import static org.junit.Assert.assertFalse;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOThread;
import org.junit.Test;

public class VariableTests extends DebuggerTest {

	@Test
	public void testNoInternalVariablesShown() throws CoreException, InterruptedException {
		runWithBreakpoints(41) //
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
}
