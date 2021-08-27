package net.oskarstrom.dashloader.core.serializer;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import net.oskarstrom.dashloader.api.serializer.DashSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DashSerializerImpl<O> implements DashSerializer<O> {
	public final String name;
	public final Path startingPath;
	private final BinarySerializer<O> serializer;

	public DashSerializerImpl(String name, BinarySerializer<O> serializer, Path startingPath) {
		this.name = name;
		this.serializer = serializer;
		this.startingPath = startingPath;
	}

	@Override
	public O deserialize(String path) throws IOException {
		final InputStream input = Files.newInputStream(startingPath.resolve(path));
		final StreamInput streamInput = StreamInput.create(input);
		final O deserialize = streamInput.deserialize(serializer);
		streamInput.close();
		return deserialize;
	}

	@Override
	public void serialize(String path, O object) throws IOException {
		final OutputStream output = Files.newOutputStream(startingPath.resolve(path));
		final StreamOutput streamOutput = StreamOutput.create(output);
		streamOutput.serialize(serializer, object);
		streamOutput.close();
	}
}
