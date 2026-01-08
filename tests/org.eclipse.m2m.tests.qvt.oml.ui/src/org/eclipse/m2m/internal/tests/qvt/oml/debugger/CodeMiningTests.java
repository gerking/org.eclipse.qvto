package org.eclipse.m2m.internal.tests.qvt.oml.debugger;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.m2m.internal.qvt.oml.debug.ui.DebugCodeMiningProvider;
import org.eclipse.m2m.qvt.oml.debug.core.QVTOThread;
import org.junit.Test;

public class CodeMiningTests extends DebuggerTest {

	@Test
	public void testCodeMininigsObjectCreation() throws CoreException, InterruptedException {
		testWithModel(TestModel.SIMPLE_UML_TO_RDB) //
				.withBreakPoints(176) //
				.checkWithEditor((event, editor) -> {
					if (event.getKind() == DebugEvent.SUSPEND && event.getSource() instanceof QVTOThread thread) {
						var provider = new DebugCodeMiningProvider();
						provider.setContext(editor);
						var future = provider.provideCodeMinings(editor.getSourceViewerOpened(), null);
						var codeMinings = future.get();

						thread.resume();

						// one extra for the model parameter code mining
						assertTrue(codeMinings.size() == 3);
						assertTrue(codeMinings.stream().anyMatch(cm -> cm.getLabel().equals("$temp_1 : ")));
						assertTrue(codeMinings.stream().anyMatch(cm -> cm.getLabel().equals("$temp_2 : ")));

						provider.dispose();
					}
				});
	}

	@Test
	public void testCodeMiningsModelParameters() throws CoreException, InterruptedException {
		testWithModel(TestModel.SIMPLE_UML_TO_RDB) //
				.withBreakPoints(41) //
				.checkWithEditor((event, editor) -> {
					if (event.getKind() == DebugEvent.SUSPEND && event.getSource() instanceof QVTOThread thread) {
						var provider = new DebugCodeMiningProvider();
						provider.setContext(editor);
						var future = provider.provideCodeMinings(editor.getSourceViewerOpened(), null);
						var codeMinings = future.get();

						thread.resume();

						assertTrue(codeMinings.size() == 1);
						assertTrue(codeMinings.stream().anyMatch(cm -> cm.getLabel().equals("$1 : ")));

						provider.dispose();
					}
				});
	}

	@Test
	public void testCodeMiningsLoopExpressions() throws CoreException, InterruptedException {
		testWithModel(TestModel.SIMPLE_UML_TO_RDB) //
				.withBreakPoints(370) //
				.checkWithEditor((event, editor) -> {
					if (event.getKind() == DebugEvent.SUSPEND && event.getSource() instanceof QVTOThread thread) {
						var provider = new DebugCodeMiningProvider();
						provider.setContext(editor);
						var future = provider.provideCodeMinings(editor.getSourceViewerOpened(), null);
						var codeMinings = future.get();

						thread.resume();

						assertTrue(codeMinings.size() == 2);
						assertTrue(codeMinings.stream().anyMatch(cm -> cm.getLabel().equals("temp1 : Packageable")));

						provider.dispose();
					}
				});
	}

}
