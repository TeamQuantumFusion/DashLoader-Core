package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.registry.*;
import dev.quantumfusion.dashloader.core.serializer.DashSerializer;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DashLoaderCore {
	public static Consumer<String> PRINT = System.out::println;
	private final Map<Class<?>, DashSerializer<?>> serializers = new HashMap<>();
	private final List<DashObjectMetadata<?, ?>> dashObjects;
	private final Path cacheFolder;
	private boolean cacheAvailable;
	private String currentSubCache;

	@SafeVarargs
	public DashLoaderCore(Path cacheFolder, Class<? extends Dashable>... dashables) {
		DashThreading.init();
		this.dashObjects = createDashObjectMetadataList(dashables);
		this.cacheFolder = cacheFolder;
		try {
			Files.createDirectories(cacheFolder);
		} catch (IOException ignored) {}
	}

	@NotNull
	private static List<DashObjectMetadata<?, ?>> createDashObjectMetadataList(Class<? extends Dashable>[] dashables) {
		var classEntries = new ArrayList<DashObjectMetadata<?, ?>>();
		for (var entry : dashables) classEntries.add(DashObjectMetadata.create(entry));
		return classEntries;
	}

	public void setCurrentSubcache(String name) {
		this.cacheAvailable = Files.exists(cacheFolder.resolve(name + "/"));
		this.currentSubCache = name;

		if (!this.cacheAvailable) {
			try {
				Files.createDirectory(cacheFolder.resolve(name + "/"));
			} catch (IOException ignored) {}
		}
	}

	public boolean isCacheMissing() {
		return !cacheAvailable;
	}

	public DashRegistryWriter createWriter(Map<Class<?>, WriteFailCallback<?, ?>> callbacks) {
		return DashRegistryBuilder.createWriter(dashObjects, callbacks);
	}

	public DashRegistryReader createReader(ChunkDataHolder... holders) {
		final DashRegistryReader reader = DashRegistryBuilder.createReader(holders);
		reader.export();
		return reader;
	}

	@SafeVarargs
	public final void prepareSerializer(Class<?> dataObject, Class<? extends Dashable>... dashables) {
		serializers.put(dataObject, DashSerializer.create(cacheFolder, dataObject, dashObjects, dashables));
	}

	@SuppressWarnings("unchecked")
	public final <O> O load(Class<O> dataObject) {
		try {
			return (O) serializers.get(dataObject).decode(currentSubCache);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public final <O> void save(O dataObject) {
		try {
			((DashSerializer<O>) serializers.get(dataObject.getClass())).encode(dataObject, currentSubCache);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
