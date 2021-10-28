package dev.quantumfusion.dashloader.core.serializer;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;

public class DashSerializer<O> {
	private final Path exportPath;
	private final HyphenSerializer<ByteBufferIO, O> serializer;

	public DashSerializer(Path exportPath, HyphenSerializer<ByteBufferIO, O> serializer) {
		this.exportPath = exportPath;
		this.serializer = serializer;
	}

	@SafeVarargs
	public static <O> DashSerializer<O> create(Path exportPath, Class<O> holderClass, DashRegistryWriter writer, Class<? extends Dashable>... dashables) {
		SerializerFactory<ByteBufferIO, O> factory = SerializerFactory.create(ByteBufferIO.class, holderClass);
		factory.addGlobalAnnotation(AbstractDataChunk.class, DataSubclasses.class, new Class[]{DataChunk.class, StagedDataChunk.class});

		for (Class<? extends Dashable> dashable : dashables) {
			final Collection dashClasses = new ArrayList(writer.getChunk(dashable).getDashClasses());
			dashClasses.remove(dashable);
			if (dashClasses.size() > 0) {
				factory.addGlobalAnnotation(dashable, DataSubclasses.class, dashClasses.toArray(Class[]::new));
			}
		}
		return new DashSerializer<>(exportPath, factory.build());
	}

	public void encode(O object) throws IOException {
		Files.createDirectories(exportPath.getParent());
		var fileChannel = FileChannel.open(exportPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
		final ByteBufferIO wrap = ByteBufferIO.wrap(fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, serializer.measure(object)));
		serializer.put(wrap, object);
	}

	public O decode() throws IOException {
		var fileChannel = FileChannel.open(exportPath);
		return serializer.get(ByteBufferIO.wrap(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())));
	}
}
