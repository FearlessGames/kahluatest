package se.fearless.kahluatest;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import se.fearless.kahluatest.luatests.LuaFailTest;
import se.mockachino.matchers.matcher.ArgumentCatcher;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;
import static se.mockachino.matchers.Matchers.match;
import static se.mockachino.matchers.MatchersBase.mAny;

public class KahluaTestRunnerLuaFailTest {

	private KahluaRunner runner;

	@Before
	public void setUp() throws Exception {
		runner = new KahluaRunner(LuaFailTest.class);
	}

	private LuaTestCase getTestCase() throws InitializationError {
		return Iterables.getOnlyElement(runner.getChildren(), null);
	}


	@Test
	public void executeCallsFail() throws Exception {
		RunNotifier notifier = mock(RunNotifier.class);
		Description description = runTest(notifier);

		ArgumentCatcher<Failure> argumentCatcher = ArgumentCatcher.create(mAny(Failure.class));
		verifyOnce().on(notifier).fireTestFailure(match(argumentCatcher));
		Failure failure = argumentCatcher.getValue();
		assertEquals(description, failure.getDescription());
		assertEquals("__concat not defined for operands: null and foo", failure.getMessage());
	}

	private Description runTest(RunNotifier notifier) throws InitializationError {
		LuaTestCase testCase = getTestCase();
		Description description = runner.describeChild(testCase);
		runner.runChild(testCase, notifier);
		return description;
	}

	@Test
	public void executeCallsFinishedEvenWhenTestFails() throws Exception {
		RunNotifier notifier = mock(RunNotifier.class);
		Description description = runTest(notifier);
		verifyOnce().on(notifier).fireTestFinished(description);
	}
}