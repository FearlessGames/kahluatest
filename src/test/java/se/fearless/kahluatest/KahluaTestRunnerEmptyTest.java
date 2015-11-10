package se.fearless.kahluatest;

import org.junit.Test;
import se.fearless.kahluatest.luatests.Empty;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class KahluaTestRunnerEmptyTest {

	@Test
	public void listTestsForEmptyClass() throws Exception {
		KahluaRunner runner = new KahluaRunner(Empty.class);
		List<LuaTestCase> children = runner.getChildren();
		assertTrue(children.isEmpty());
	}
}
