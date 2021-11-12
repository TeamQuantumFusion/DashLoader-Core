package dev.quantumfusion.dashloader.core.io;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.io.serializer.DashSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The IO Module of DashLoaderCore. Handles Serializers and Caches.
 */
public final class IOHandler {
	private final Map<Class<?>, DashSerializer<?>> serializers = new HashMap<>();
	private final Map<String, CacheArea> caches = new HashMap<>();

	private final List<DashObjectClass<?, ?>> dashObjects;
	private final Path cacheDir;

	private CacheArea cacheArea;
	private SubCacheArea subCacheArea;

	public IOHandler(List<DashObjectClass<?, ?>> dashObjects, String password, Path cacheDir) {
		this.dashObjects = dashObjects;
		this.cacheDir = cacheDir;
		if (!password.equals("DashLoaderCore property. >w<")) {
			throw new RuntimeException("You cannot initialize DashConfigHandler. git gud.");
		}
	}

	@SafeVarargs
	public final void addSerializer(Class<?> dataObject, Class<? extends Dashable<?>>... dashables) {
		this.serializers.put(dataObject, DashSerializer.create(cacheDir, dataObject, dashObjects, dashables));
	}

	public void setCacheArea(String name) {
		this.cacheArea = new CacheArea(new ArrayList<>(), name);
	}

	public void setSubCacheArea(String name) {
		if (cacheArea == null) throw new RuntimeException("Current Cache Area has not been set.");
		this.subCacheArea = new SubCacheArea(name);
	}

	public boolean cacheExists() {
		return Files.exists(getCurrentCachePath());
	}

	public <O> O load(Class<O> dataObject) {
		try {
			return (O) this.serializers.get(dataObject).decode(getCurrentCachePath());
		} catch (IOException e) {
			cacheArea.clear(cacheDir);
			caches.remove(cacheArea.name);
			throw new RuntimeException(e);
		}
	}

	public <O> void save(O dataObject) {
		try {
			((DashSerializer<O>) this.serializers.get(dataObject.getClass())).encode(dataObject, getCurrentCachePath());
		} catch (IOException e) {
			cacheArea.clear(cacheDir);
			caches.remove(cacheArea.name);
			throw new RuntimeException(e);
		}
	}

	private Path getCurrentCachePath() {
		return cacheArea.getPath(cacheDir, subCacheArea);
	}
}
