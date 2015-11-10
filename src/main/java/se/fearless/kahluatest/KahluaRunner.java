package se.fearless.kahluatest;

import com.google.common.collect.Maps;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import se.fearless.common.lua.LuaRuntimeErrorListener;
import se.fearless.common.lua.LuaVm;
import se.fearless.kahluatest.annotations.LuaTest;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaClosure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KahluaRunner extends ParentRunner<LuaTestCase> {
	private static final String LUA_BEFORE_FUNCTION = "before";
	private static final String LUA_AFTER_FUNCTION = "after";
	private static final String TEST_PREFIX = "test";

	private Map<String, Object> luaTestFunctions;
	private LuaVm kahluaVm;

	private Map<Class<?>, Object> classesToExpose = Maps.newHashMap();

	public KahluaRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		addClassToExpose(new JunitApiExposer());
	}

	public <T> void addClassToExpose(T instance) {
		Object put = classesToExpose.put(instance.getClass(), instance);
		if (put != null) {
			System.out.println("Replaced " + put);
		}
	}


	@Override
	protected List<LuaTestCase> getChildren() {
		List<LuaTestCase> children = new ArrayList<LuaTestCase>();
		setupLuaVm();
		for (Map.Entry<String, Object> entry : luaTestFunctions.entrySet()) {
			children.add(new LuaTestCase(entry.getKey(), (LuaClosure) entry.getValue()));
		}
		return children;
	}

	@Override
	protected Description describeChild(LuaTestCase child) {
		return Description.createTestDescription(getTestClass().getJavaClass(), child.getName());
	}

	@Override
	protected void runChild(LuaTestCase child, RunNotifier notifier) {
		Description description = describeChild(child);
		notifier.fireTestStarted(description);
		try {
			kahluaVm.runClosure(child.getClosure());
		} catch (RuntimeException e) {
			notifier.fireTestFailure(new Failure(description, e));
		} finally {
			notifier.fireTestFinished(description);
		}
	}

	private void setupLuaVm() {
		LuaTest annotation = getTestClass().getJavaClass().getAnnotation(LuaTest.class);
		String luaSourceFile = annotation.source();

		if (luaSourceFile.isEmpty()) {
			luaSourceFile = getTestClass().getJavaClass().getName();
			luaSourceFile = luaSourceFile.substring(luaSourceFile.lastIndexOf(".") + 1) + ".lua";
		}


		kahluaVm = new LuaVm(new LuaSourceProviderImpl());
		kahluaVm.setRuntimeErrorListener(new LuaRuntimeErrorListener() {
			@Override
			public void runtimeError(Object o, String s, String s1, Exception e) {
				throw new RuntimeException(s, e);
			}
		});
		for (Object instance : classesToExpose.values()) {
			kahluaVm.getExposer().exposeGlobalFunctions(instance);
		}
		luaTestFunctions = Maps.newHashMap();


		boolean result = kahluaVm.runLua(luaSourceFile);


		KahluaTableIterator it = kahluaVm.getEnvironment().iterator();

		while (it.advance()) {
			Object value = it.getValue();
			String name = it.getKey().toString();
			String valueType = KahluaUtil.type(value);

			boolean foundMatch = (name.startsWith(TEST_PREFIX)
					|| name.equals(LUA_BEFORE_FUNCTION)
					|| name.equals(LUA_AFTER_FUNCTION))
					&& valueType.equals("function");
			if (foundMatch) {
				luaTestFunctions.put(name, value);
			}
		}
	}
}
