package dev.quantumfusion.dashloader.core.io;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.io.serializer.DashSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The IO Module of DashLoaderCore. Handles Serializers and Caches.
 */
public final class IOHandler {
	private final Map<Class<?>, DashSerializer<?>> serializers = new HashMap<>();

	private final List<DashObjectClass<?, ?>> dashObjects;
	private final Path cacheDir;

	private String cacheArea;
	private String subCacheArea;

	public IOHandler(List<DashObjectClass<?, ?>> dashObjects, String password, Path cacheDir) {
		this.dashObjects = dashObjects;
		this.cacheDir = cacheDir;
		if (!password.equals("DashLoaderCore property. >w<")) {
			throw new RuntimeException("You cannot initialize DashConfigHandler. git gud.");
		}
	}

	@SafeVarargs
	public final void addSerializer(Class<?> dataObject, Class<? extends Dashable<?>>... dashables) {
		this.serializers.put(dataObject, DashSerializer.create(getCurrentCacheDir(), dataObject, dashObjects, dashables));
	}

	public void setCacheArea(String name) {
		this.cacheArea = name;
	}

	public void setSubCacheArea(String name) {
		this.subCacheArea = name;
	}

	public void clearCache() {
		try {
			Path dir = getCurrentSubCacheDir();
			Files.list(dir).forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			Files.delete(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean cacheExists() {
		return Files.exists(getCurrentSubCacheDir());
	}

	public <O> O load(Class<O> dataObject) {
		try {
			return (O) this.serializers.get(dataObject).decode(getCurrentSubCacheDir());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <O> void save(O dataObject) {
		try {
			((DashSerializer<O>) this.serializers.get(dataObject.getClass())).encode(dataObject, getCurrentSubCacheDir());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Path getCurrentSubCacheDir() {
		if (subCacheArea == null) throw new RuntimeException("Current SubCache has not been set.");
		return getCurrentCacheDir().resolve(subCacheArea + "/");
	}

	private Path getCurrentCacheDir() {
		if (cacheArea == null) throw new RuntimeException("Current Cache has not been set.");
		return cacheDir.resolve(cacheArea + "/");
	}
}
