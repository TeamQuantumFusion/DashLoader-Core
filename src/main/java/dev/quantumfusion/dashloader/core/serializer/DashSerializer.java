package dev.quantumfusion.dashloader.core.serializer;

import dev.quantumfusion.dashloader.core.DashObjectMetadata;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.hyphen.ClassDefiner;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashSerializer<O> {
	private final Class<O> dataClass;
	private final Path cacheFolder;
	private final HyphenSerializer<UnsafeIO, O> serializer;

	public DashSerializer(Class<O> dataClass, Path cacheFolder, HyphenSerializer<UnsafeIO, O> serializer) {
		this.dataClass = dataClass;
		this.cacheFolder = cacheFolder;
		this.serializer = serializer;
	}

	@SafeVarargs
	public static <O> DashSerializer<O> create(Path cacheFolder, Class<O> holderClass, List<DashObjectMetadata<?, ?>> dashObjects, Class<? extends Dashable>... dashables) {
		var serializerFileLocation = cacheFolder.resolve(holderClass.getSimpleName().toLowerCase() + ".dlc");
		prepareFile(serializerFileLocation);
		if (Files.exists(serializerFileLocation)) {
			var classDefiner = new ClassDefiner(Thread.currentThread().getContextClassLoader());
			try {
				classDefiner.def(getSerializerName(holderClass), Files.readAllBytes(serializerFileLocation));
				return new DashSerializer<>(holderClass, cacheFolder, (HyphenSerializer<UnsafeIO, O>) ClassDefiner.SERIALIZER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Files.createFile(serializerFileLocation);
		} catch (IOException ignored) {}

		var factory = SerializerFactory.createDebug(UnsafeIO.class, holderClass);
		factory.addGlobalAnnotation(AbstractDataChunk.class, DataSubclasses.class, new Class[]{DataChunk.class, StagedDataChunk.class});
		factory.setClassName(getSerializerName(holderClass));
		factory.setExportPath(serializerFileLocation);
		for (Class<? extends Dashable> dashable : dashables) {
			var dashClasses = new ArrayList<Class<?>>();
			for (var dashObject : dashObjects)
				if (dashable == dashObject.dashType) dashClasses.add(dashObject.dashClass);

			dashClasses.remove(dashable);
			if (dashClasses.size() > 0)
				factory.addGlobalAnnotation(dashable, DataSubclasses.class, dashClasses.toArray(Class[]::new));
		}
		return new DashSerializer<>(holderClass, cacheFolder, factory.build());

	}

	@NotNull
	private static <O> String getSerializerName(Class<O> holderClass) {
		return holderClass.getSimpleName().toLowerCase() + "-serializer";
	}

	private static void prepareFile(Path path) {
		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void encode(O object, String subCache) throws IOException {
		final Path filePath = getFilePath(subCache);
		prepareFile(filePath);
		try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			final var wrap = UnsafeIO.wrap(channel.map(FileChannel.MapMode.READ_WRITE, 0, serializer.measure(object)));
			serializer.put(wrap, object);
		}
	}

	@NotNull
	private Path getFilePath(String subCache) {
		return cacheFolder.resolve(subCache + "/" + dataClass.getSimpleName().toLowerCase(Locale.ROOT) + ".dld");
	}

	public O decode(String subCache) throws IOException {
		final Path filePath = getFilePath(subCache);
		prepareFile(filePath);
		try (FileChannel channel = FileChannel.open(filePath)) {
			return serializer.get(UnsafeIO.wrap(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())));
		}
	}
}
