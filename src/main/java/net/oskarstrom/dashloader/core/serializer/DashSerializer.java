package net.oskarstrom.dashloader.core.serializer;

import io.activej.serializer.BinarySerializer;

public class DashSerializer<T> {
	private final String name;
	private final BinarySerializer<T> serializer;

	public DashSerializer(String name, BinarySerializer<T> serializer) {
		this.name = name;
		this.serializer = serializer;
	}
}
