package net.oskarstrom.dashloader.core.serializer;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.stream.StreamOutput;
import net.oskarstrom.dashloader.api.serializer.DashSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DashSerializerImpl<O> implements DashSerializer<O> {
	private final String name;
	private final BinarySerializer<O> serializer;

	public DashSerializerImpl(String name, BinarySerializer<O> serializer) {
		this.name = name;
		this.serializer = serializer;
	}

	public String getName() {
		return name;
	}

	@Override
	public O deserialize(Path path) throws IOException {
		final byte[] bytes = Files.readAllBytes(path);
		return serializer.decode(bytes, 0);
	}

	@Override
	public void serialize(Path path, O object) throws IOException {
		final OutputStream output = Files.newOutputStream(path);
		final StreamOutput streamOutput = StreamOutput.create(output);
		streamOutput.serialize(serializer, object);
		streamOutput.close();
	}
}
