package net.oskarstrom.dashloader.core.serializer;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import net.oskarstrom.dashloader.core.PathConstants;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DashSerializer<O> {
	public final String name;
	public final Path startingPath;
	private final HyphenSerializer<ByteBufferIO, O> serializer;

	public DashSerializer(String name, HyphenSerializer<ByteBufferIO, O> serializer, Path startingPath) {
		this.name = name;
		this.serializer = serializer;
		this.startingPath = startingPath;
	}

	public O deserialize() throws IOException {
		var fileChannel = FileChannel.open(startingPath.resolve(name + PathConstants.DATA_EXTENSION));
		return serializer.get(ByteBufferIO.wrap(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())));
	}

	public void serialize(O object) throws IOException {
		final Path resolve = startingPath.resolve(name + PathConstants.DATA_EXTENSION);
		Files.createDirectories(startingPath);
		var fileChannel = FileChannel.open(resolve, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
		final ByteBufferIO wrap = ByteBufferIO.wrap(fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, serializer.measure(object)));
		serializer.put(wrap, object);
	}
}
