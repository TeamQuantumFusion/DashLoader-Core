package net.oskarstrom.dashloader.core.serializer;

import io.activej.codegen.ClassBuilder;
import io.activej.codegen.DefiningClassLoader;
import io.activej.serializer.BinarySerializer;
import io.activej.serializer.CompatibilityLevel;
import io.activej.serializer.SerializerBuilder;
import net.oskarstrom.dashloader.core.util.ClassLoaderHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DashSerializerManager {
	private final Path systemCacheFolder;
	private final SerializerMap<?> serializers = new SerializerMap<>(new HashMap<>());
	private final Map<Class<?>, Set<Class<?>>> subclasses = new HashMap<>();

	public DashSerializerManager(Path systemCacheFolder) {
		preparePath(systemCacheFolder);
		this.systemCacheFolder = systemCacheFolder;
	}

	@NotNull
	public <T> DashSerializer<T> getSerializer(Class<T> klazz) {
		//noinspection unchecked
		var serializer = ((SerializerMap<T>) serializers).get(klazz);
		if (serializer == null)
			throw new IllegalStateException("Serializer not registered for " + klazz.getSimpleName() + "! Are you sure if you've registered your serializers?");
		return serializer;
	}

	public <T> DashSerializer<T> loadOrCreateSerializer(String serializerName, Class<T> klazz, Path startingPath, Class<?>... keys) {
		BinarySerializer<T> serializer;
		try {
			//noinspection unchecked
			Class<BinarySerializer<T>> klaz = (Class<BinarySerializer<T>>) ClassLoaderHelper.findClass(getSerializerFqcn(serializerName));
			// if the class is already loaded, create the serializer directly
			serializer = createBinarySerializer(klaz);
		} catch (ClassNotFoundException e) {
			// if the serializer class is not loaded, then try and load the serializer from the file system
			serializer = loadSerializer(serializerName);
			if (serializer == null) {
				// if we can't do that then create a new serializer
				serializer = createSerializer(serializerName, klazz, keys);
			}
		}
		preparePath(startingPath);
		final DashSerializer<T> dashSerializer = new DashSerializer<>(serializerName, serializer, startingPath);
		//noinspection unchecked
		((SerializerMap<T>) serializers).put(klazz, dashSerializer);
		return dashSerializer;
	}

	private void preparePath(Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				//TODO error handling
				e.printStackTrace();
			}
		}
	}

	private <T> BinarySerializer<T> loadSerializer(String serializerName) {
		//TODO change to dlc when activej merged my pr
		Path path = systemCacheFolder.resolve(serializerName + ".class");
		if (!Files.exists(path))
			return null;
		try {
			byte[] bytes = Files.readAllBytes(path);
			//noinspection unchecked
			Class<BinarySerializer<T>> serializerClass = (Class<BinarySerializer<T>>) ClassLoaderHelper.defineClass(serializerName, bytes, 0, bytes.length);
			return createBinarySerializer(serializerClass);

		} catch (IOException e) {
			// fuck checked exceptions lmao
			throw new RuntimeException(e);
		}
	}

	private <T> BinarySerializer<T> createSerializer(String serializerName, Class<T> klazz, Class<?>... keys) {
		SerializerBuilder builder = SerializerBuilder.create(DefiningClassLoader.create(ClassLoaderHelper.accessor))
				.withClassName(serializerName)
				//TODO change to dlc when activej merged my pr
				.withGeneratedBytecodePath(systemCacheFolder)
				.withAnnotationCompatibilityMode()
				.withCompatibilityLevel(CompatibilityLevel.LEVEL_3_LE);
		for (Class<?> key : keys) {
			final var set = subclasses.get(key);
			if (set == null)
				throw new IllegalArgumentException("Key \"" + key + "\" not found in subclass registry! This is likely a mistake!");
			//noinspection unchecked plz stfu
			builder.withSubclasses(key, new ArrayList(set));
		}
		return builder.build(klazz);
	}

	private String getSerializerFqcn(String name) {
		return "io.activej.codegen.io.activej.serializer" + name;
	}

	public void addSubclass(Class<?> key, Class<?> klazz) {
		getSubclassesWithKey(key).add(klazz);
	}

	public void addSubclasses(Class<?> key, Collection<Class<?>> classes) {
		getSubclassesWithKey(key).addAll(classes);
	}

	public void addSubclasses(Class<?> key, Class<?>... classes) {
		getSubclassesWithKey(key).addAll(List.of(classes));
	}

	private Set<Class<?>> getSubclassesWithKey(Class<?> key) {
		return subclasses.computeIfAbsent(key, ignored -> new HashSet<>());
	}

	private <T> BinarySerializer<T> createBinarySerializer(Class<BinarySerializer<T>> serializerClass) {
		// check if class is actually built/generated
		try {
			Field field = serializerClass.getField(ClassBuilder.CLASS_BUILDER_MARKER);
			//noinspection ResultOfMethodCallIgnored
			field.get(null);
			return serializerClass.getConstructor().newInstance();
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
			// fuck checked exceptions lmao
			throw new AssertionError(e);
		}
	}

	private static class SerializerMap<T> extends AbstractMap<Class<T>, DashSerializer<T>> {

		private final Map<Class<T>, DashSerializer<T>> delegate;

		private SerializerMap(Map<Class<T>, DashSerializer<T>> delegate) {
			this.delegate = delegate;
		}

		@NotNull
		@Override
		public Set<Entry<Class<T>, DashSerializer<T>>> entrySet() {
			return delegate.entrySet();
		}

		@Override
		public DashSerializer<T> put(Class<T> key, DashSerializer<T> value) {
			return delegate.put(key, value);
		}
	}
}
