package se.fearless.kahluatest;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import se.fearless.kahluatest.luatests.SimpleCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;

public class KahluaTestRunnerSimpleCaseTest {

	private KahluaRunner runner;

	@Before
	public void setUp() throws Exception {
		runner = new KahluaRunner(SimpleCase.class);
	}

	private LuaTestCase getTestCaseForSimple() throws InitializationError {
		return Iterables.getOnlyElement(runner.getChildren(), null);
	}

	@Test
	public void listTestsSimpleCase() throws Exception {
		LuaTestCase testCase = getTestCaseForSimple();
		assertNotNull(testCase);
	}

	@Test
	public void testCasesNames() throws Exception {
		LuaTestCase testCase = getTestCaseForSimple();
		assertEquals("testOne", testCase.getName());
	}

	@Test
	public void getDescriptionForSimpleCase() throws Exception {
		LuaTestCase testCase = getTestCaseForSimple();
		Description description = runner.describeChild(testCase);
		assertEquals("se.fearless.kahluatest.luatests.SimpleCase", description.getClassName());
		assertEquals("testOne", description.getMethodName());
		assertEquals("testOne(se.fearless.kahluatest.luatests.SimpleCase)", description.getDisplayName());
	}

	@Test
	public void executeCallsInProgress() throws Exception {
		RunNotifier notifier = mock(RunNotifier.class);
		Description description = runTestCase(notifier);
		verifyOnce().on(notifier).fireTestStarted(description);
	}

	private Description runTestCase(RunNotifier notifier) throws InitializationError {
		LuaTestCase testCase = getTestCaseForSimple();
		Description description = runner.describeChild(testCase);
		runner.runChild(testCase, notifier);
		return description;
	}

	@Test
	public void executeCallsFinished() throws Exception {
		RunNotifier notifier = mock(RunNotifier.class);
		Description description = runTestCase(notifier);
		verifyOnce().on(notifier).fireTestFinished(description);
	}
}