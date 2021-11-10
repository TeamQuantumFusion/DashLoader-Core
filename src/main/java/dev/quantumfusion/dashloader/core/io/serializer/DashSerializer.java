package dev.quantumfusion.dashloader.core.io.serializer;

import dev.quantumfusion.dashloader.core.DashLoaderCore;
import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.progress.ProgressHandler;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.hyphen.ClassDefiner;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashSerializer<O> {
	private final Class<O> dataClass;
	private final HyphenSerializer<UnsafeIO, O> serializer;
	@Nullable
	private final SerializerCompressor compressor;

	public DashSerializer(Class<O> dataClass, HyphenSerializer<UnsafeIO, O> serializer) {
		this.dataClass = dataClass;
		this.serializer = serializer;
		this.compressor = SerializerCompressor.create(DashLoaderCore.CORE.getConfigHandler().config.compression);
	}

	@SafeVarargs
	public static <O> DashSerializer<O> create(Path cacheArea, Class<O> holderClass, List<DashObjectClass<?, ?>> dashObjects, Class<? extends Dashable>... dashables) {
		var serializerFileLocation = cacheArea.resolve(holderClass.getSimpleName().toLowerCase() + ".dlc");
		prepareFile(serializerFileLocation);
		if (Files.exists(serializerFileLocation)) {
			var classDefiner = new ClassDefiner(Thread.currentThread().getContextClassLoader());
			try {
				classDefiner.def(getSerializerName(holderClass), Files.readAllBytes(serializerFileLocation));
				return new DashSerializer<>(holderClass, (HyphenSerializer<UnsafeIO, O>) ClassDefiner.SERIALIZER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Files.createFile(serializerFileLocation);
		} catch (IOException ignored) {}

		var factory = SerializerFactory.create(UnsafeIO.class, holderClass);
		factory.addGlobalAnnotation(AbstractDataChunk.class, DataSubclasses.class, new Class[]{DataChunk.class, StagedDataChunk.class});
		factory.setClassName(getSerializerName(holderClass));
		factory.setExportPath(serializerFileLocation);
		for (Class<? extends Dashable> dashable : dashables) {
			var dashClasses = new ArrayList<Class<?>>();
			for (var dashObject : dashObjects)
				if (dashable == dashObject.getTag()) dashClasses.add(dashObject.getDashClass());

			dashClasses.remove(dashable);
			if (dashClasses.size() > 0)
				factory.addGlobalAnnotation(dashable, DataSubclasses.class, dashClasses.toArray(Class[]::new));
		}
		return new DashSerializer<>(holderClass, factory.build());

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

	public void encode(O object, Path subCache) throws IOException {
		final ProgressHandler progress = DashLoaderCore.CORE.getProgressHandler();
		progress.setCurrentTask("Saving " + dataClass.getSimpleName()).startSubTask(4);
		prepareFile(subCache);

		final int measure = serializer.measure(object);
		final ByteBuffer rawEncode = ByteBuffer.allocateDirect(measure);
		final var wrap = UnsafeIO.wrap(rawEncode);
		progress.completedSubTask();

		serializer.put(wrap, object);
		progress.completedSubTask();

		final ByteBuffer out;
		if (compressor != null) {
			out = ByteBuffer.allocateDirect(compressor.maxLength(measure) + 4);
			out.putInt(measure);
			compressor.compress(rawEncode, out);
			out.limit(out.position());
			out.rewind();
		} else out = rawEncode;
		progress.completedSubTask();

		try (FileChannel channel = FileChannel.open(subCache, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			channel.map(FileChannel.MapMode.READ_WRITE, 0, out.remaining()).put(out);
		}
		progress.completedSubTask();
	}


	@NotNull
	private Path getFilePath(Path subCache) {
		return subCache.resolve(dataClass.getSimpleName().toLowerCase(Locale.ROOT) + ".dld");
	}

	public O decode(Path subCache) throws IOException {

		prepareFile(subCache);

		try (FileChannel channel = FileChannel.open(subCache)) {
			var map = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

			ByteBuffer decoded;
			if (compressor != null) {
				decoded = ByteBuffer.allocateDirect(map.getInt());
				compressor.decompress(map, decoded);
			} else decoded = map;

			return serializer.get(UnsafeIO.wrap(decoded));
		}
	}
}
