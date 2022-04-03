package dev.quantumfusion.dashloader.core.io.serializer;

import com.github.luben.zstd.Zstd;
import dev.quantumfusion.dashloader.core.DashLoaderCore;
import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.progress.ProgressHandler;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.dashloader.core.util.DashUtil;
import dev.quantumfusion.hyphen.ClassDefiner;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.taski.Task;
import dev.quantumfusion.taski.builtin.StepTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DashSerializer<O> {

	private static final int HEADER_SIZE = 5;
	private final Class<O> dataClass;
	private final HyphenSerializer<UnsafeIO, O> serializer;
	private final byte compressionLevel;

	public DashSerializer(Class<O> dataClass, HyphenSerializer<UnsafeIO, O> serializer) {
		this.dataClass = dataClass;
		this.serializer = serializer;
		this.compressionLevel = DashLoaderCore.CONFIG.config.compression;
	}

	public static <F> DashSerializer<F> create(Path cacheArea, Class<F> holderClass, List<DashObjectClass<?, ?>> dashObjects, Class<? extends Dashable<?>>[] dashables) {
		var serializerFileLocation = cacheArea.resolve(holderClass.getSimpleName().toLowerCase() + ".dlc");
		prepareFile(serializerFileLocation);
		if (Files.exists(serializerFileLocation)) {
			var classDefiner = new ClassDefiner(Thread.currentThread().getContextClassLoader());
			try {
				classDefiner.def(getSerializerName(holderClass), Files.readAllBytes(serializerFileLocation));
				return new DashSerializer<>(holderClass, (HyphenSerializer<UnsafeIO, F>) ClassDefiner.SERIALIZER);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		var factory = SerializerFactory.createDebug(UnsafeIO.class, holderClass);
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

	public void encode(O object, Path subCache, @Nullable Consumer<Task> taskConsumer) throws IOException {
		StepTask task = new StepTask(dataClass.getSimpleName(),  compressionLevel > 0 ? 5 : 2);
		if (taskConsumer != null) {
			taskConsumer.accept(task);
		}

		final Path outPath = getFilePath(subCache);
		prepareFile(outPath);


		try (FileChannel channel = FileChannel.open(outPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			final int rawFileSize = serializer.measure(object);

			if (compressionLevel > 0) {
				// Allocate
				final long maxSize =	Zstd.compressBound(rawFileSize);
				final var dst = UnsafeIO.create((int) maxSize);
				final var src = UnsafeIO.create(rawFileSize);
				task.next();

				// Serialize
				serializer.put(src, object);

				task.next();

				// Compress
				src.rewind();
				final long size = Zstd.compressUnsafe(dst.address(), maxSize, src.address(), rawFileSize, compressionLevel);
				task.next();

				// Write
				final var map = channel.map(FileChannel.MapMode.READ_WRITE, 0, size + HEADER_SIZE);
				task.next();

				final var file = UnsafeIO.wrap(map);
				file.putByte(compressionLevel);
				file.putInt(rawFileSize);
				getUnsafeInstance().copyMemory(dst.address(), file.address() + HEADER_SIZE, size);
				src.close();
				dst.close();
			} else {
				final var map = channel.map(FileChannel.MapMode.READ_WRITE, 0, rawFileSize + 1);
				task.next();
				UnsafeIO file = UnsafeIO.wrap(map);
				file.putByte(compressionLevel);
				serializer.put(file, object);
			}
		}
		task.next();
	}

	private static sun.misc.Unsafe getUnsafeInstance() {
		Class<sun.misc.Unsafe> clazz = sun.misc.Unsafe.class;
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.getType().equals(clazz))
				continue;
			final int modifiers = field.getModifiers();
			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)))
				continue;
			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			break;
		}

		throw new IllegalStateException("Unsafe is unavailable.");
	}


	@NotNull
	private Path getFilePath(Path subCache) {
		return subCache.resolve(dataClass.getSimpleName().toLowerCase() + ".dld");
	}

	public O decode(Path subCache, @Nullable Consumer<Task> taskConsumer) throws IOException {
		prepareFile(subCache);

		StepTask task = new StepTask(dataClass.getSimpleName(), 4);
		if (taskConsumer != null) {
			taskConsumer.accept(task);
		}

		try (FileChannel channel = FileChannel.open(getFilePath(subCache))) {
			task.next();
			var map = UnsafeIO.wrap(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
			task.next();

			byte compression = map.getByte();
			if (compression > 0) {
				final int size = map.getInt();
				final var dst = UnsafeIO.create(size);
				Zstd.decompressUnsafe(dst.address(), size, map.address() + HEADER_SIZE, channel.size() - HEADER_SIZE);
				task.next();
				O object = serializer.get(dst);
				task.next();
				dst.close();

				return object;
			} else {
				task.next();
				O object = serializer.get(map);
				task.next();
				return object;
			}
		}
	}
}
