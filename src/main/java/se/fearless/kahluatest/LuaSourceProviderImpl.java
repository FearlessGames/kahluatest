package se.fearless.kahluatest;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import se.fearless.common.io.InputReaderSupplier;
import se.krka.kahlua.require.LuaSourceProvider;

import java.io.*;
import java.net.URL;
import java.util.function.Supplier;

public class LuaSourceProviderImpl implements LuaSourceProvider {

	public Reader getLuaSource(String key) {
		try {
			if (!key.endsWith(".lua")) {
				key = key + ".lua";
			}
			URL url = Resources.getResource(key);

			return new FileReader(url.getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
