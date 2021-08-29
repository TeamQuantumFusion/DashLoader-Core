package net.oskarstrom.dashloader.core.serializer;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;
import net.oskarstrom.dashloader.core.PathConstants;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DashSerializer<O> {
	public final String name;
	public final Path startingPath;
	private final BinarySerializer<O> serializer;

	public DashSerializer(String name, BinarySerializer<O> serializer, Path startingPath) {
		this.name = name;
		this.serializer = serializer;
		this.startingPath = startingPath;
	}

	public O deserialize(@Nullable String fileName) throws IOException {
		final String path = fileName == null ? name + PathConstants.DATA_EXTENSION : fileName + PathConstants.DATA_EXTENSION;
		final InputStream input = Files.newInputStream(startingPath.resolve(path));
		final StreamInput streamInput = StreamInput.create(input);
		final O deserialize = streamInput.deserialize(serializer);
		streamInput.close();
		return deserialize;
	}

	public void serialize(@Nullable String fileName, O object) throws IOException {
		final String path = fileName == null ? name + PathConstants.DATA_EXTENSION : fileName + PathConstants.DATA_EXTENSION;
		final OutputStream output = Files.newOutputStream(startingPath.resolve(path));
		final StreamOutput streamOutput = StreamOutput.create(output);
		streamOutput.serialize(serializer, object);
		streamOutput.close();
	}
}
