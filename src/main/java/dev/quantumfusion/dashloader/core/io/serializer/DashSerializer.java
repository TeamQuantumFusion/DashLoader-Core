package dev.quantumfusion.dashloader.core.io.serializer;

import dev.quantumfusion.dashloader.core.DashLoaderCore;
import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.progress.ProgressHandler;
import dev.quantumfusion.dashloader.core.progress.task.CountTask;
import dev.quantumfusion.dashloader.core.progress.task.DynamicTask;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.hyphen.ClassDefiner;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class DashSerializer<O> {
	private final Class<O> dataClass;
	private final HyphenSerializer<ByteBufferIO, O> serializer;
	@Nullable
	private final SerializerCompressor compressor;

	public DashSerializer(Class<O> dataClass, HyphenSerializer<ByteBufferIO, O> serializer) {
		this.dataClass = dataClass;
		this.serializer = serializer;
		this.compressor = SerializerCompressor.create(DashLoaderCore.CONFIG.config.compression);
	}

	public static <F> DashSerializer<F> create(Path cacheArea, Class<F> holderClass, List<DashObjectClass<?, ?>> dashObjects, Class<? extends Dashable<?>>[] dashables) {
		var serializerFileLocation = cacheArea.resolve(holderClass.getSimpleName().toLowerCase() + ".dlc");
		prepareFile(serializerFileLocation);
		if (Files.exists(serializerFileLocation)) {
			var classDefiner = new ClassDefiner(Thread.currentThread().getContextClassLoader());
			try {
				classDefiner.def(getSerializerName(holderClass), Files.readAllBytes(serializerFileLocation));
				return new DashSerializer<>(holderClass, (HyphenSerializer<ByteBufferIO, F>) ClassDefiner.SERIALIZER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, holderClass);
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
		final ProgressHandler progress = DashLoaderCore.PROGRESS;

		CountTask task = new CountTask(compressor == null ? 2 : 4);
		progress.getCurrentContext().setSubtask(task);
		final Path outPath = getFilePath(subCache);
		prepareFile(outPath);


		try (FileChannel channel = FileChannel.open(outPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			final int rawFileSize = serializer.measure(object);

			if (compressor != null) {
				final var out = ByteBuffer.allocateDirect(compressor.maxLength(rawFileSize) + 4);
				final var byteBuffer = ByteBuffer.allocateDirect(rawFileSize);
				final var byteBufferTask = new DynamicTask(() -> byteBuffer.position() / (double) rawFileSize);
				task.completedTask();


				task.setSubtask(byteBufferTask);
				serializer.put(ByteBufferIO.wrap(byteBuffer), object);
				task.completedTask();

				byteBuffer.rewind();

				task.setSubtask(byteBufferTask);
				compressor.compress(byteBuffer, out);
				task.completedTask();

				final int position = out.position();
				out.limit(position);
				final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, position + 4);
				map.putInt(rawFileSize); // actual size
				out.rewind();
				map.put(out);
			} else {
				final var map = channel.map(FileChannel.MapMode.READ_WRITE, 0, rawFileSize);
				final var byteBufferTask = new DynamicTask(() -> map.position() / (double) rawFileSize);
				task.completedTask();

				task.setSubtask(byteBufferTask);
				serializer.put(ByteBufferIO.wrap(map), object);

			}
		}
		task.completedTask();
	}


	@NotNull
	private Path getFilePath(Path subCache) {
		return subCache.resolve(dataClass.getSimpleName().toLowerCase() + ".dld");
	}

	public O decode(Path subCache) throws IOException {
		prepareFile(subCache);

		try (FileChannel channel = FileChannel.open(getFilePath(subCache))) {
			final long size = channel.size();
			var map = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
			ByteBuffer decoded;
			if (compressor != null) {
				decoded = ByteBuffer.allocateDirect(map.getInt());
				compressor.decompress(map, decoded);
				decoded.rewind();
			} else decoded = map;

			return serializer.get(ByteBufferIO.wrap(decoded));
		}
	}
}
