package se.fearless.kahluatest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import se.fearless.kahluatest.luatests.ApiExposer;

import java.util.List;

import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;

public class JunitApiExposerTest {

	private KahluaRunner runner;
	private ImmutableMap<String, LuaTestCase> testCases;
	private RunNotifier notifier;
	private JunitApiExposer junitApiExposer;

	@Before
	public void setUp() throws Exception {
		runner = new KahluaRunner(ApiExposer.class);
		List<LuaTestCase> tests = runner.getChildren();
		testCases = Maps.uniqueIndex(tests, new Function<LuaTestCase, String>() {
			@Override
			public String apply(LuaTestCase input) {
				return input.getName();
			}
		});
		notifier = mock(RunNotifier.class);

	}

	@Test
	public void assertEquals() throws Exception {
		LuaTestCase testCase = testCases.get("testAssertEquals");
		Description description = runner.describeChild(testCase);
		runner.runChild(testCase, notifier);
		verifyOnce().on(notifier).fireTestFinished(description);
	}
}
