package se.fearless.kahluatest;

import se.krka.kahlua.vm.LuaClosure;

public class LuaTestCase {
	private final String name;
	private final LuaClosure closure;

	public LuaTestCase(String name, LuaClosure closure) {
		this.name = name;
		this.closure = closure;
	}

	public String getName() {
		return name;
	}

	public LuaClosure getClosure() {
		return closure;
	}
}
