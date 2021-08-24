package net.oskarstrom.dashloader.core;

import io.activej.serializer.SerializerBuilder;

public class TestUtils {

	public static SerializerBuilder builder = SerializerBuilder.create();

	public static void test(Class<?>... classes) {
		for (Class<?> aClass : classes) {
			builder.build(aClass);
		}
	}
}
