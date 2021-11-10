package dev.quantumfusion.dashloader.core.io;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.io.serializer.DashSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The IO Module of DashLoaderCore. Handles Serializers and Caches.
 *
 * @param <F> Data File Object
 */
public final class IOHandler<F> {
	private final Map<Class<F>, DashSerializer<F>> serializers = new HashMap<>();
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
	public final void addSerializer(Class<F> dataObject, Class<? extends Dashable<?>>... dashables) {
		this.serializers.put(dataObject, DashSerializer.create(cacheDir, dataObject, dashObjects, dashables));
	}

	public void setCacheArea(String name) {
		this.cacheArea = caches.computeIfAbsent(name, s -> new CacheArea(new ArrayList<>(), s));
	}

	public void setSubCacheArea(String name) {
		if (cacheArea == null) throw new RuntimeException("Current Cache Area has not been set.");
		this.subCacheArea = cacheArea.subCachesMap.computeIfAbsent(name, SubCacheArea::new);
	}

	public F load(Class<F> dataObject) {
		try {
			return this.serializers.get(dataObject).decode(cacheArea.getPath(cacheDir, subCacheArea));
		} catch (IOException e) {
			cacheArea.clear(cacheDir);
			caches.remove(cacheArea.name);
			throw new RuntimeException(e);
		}
	}

	public void save(F dataObject) {
		try {
			this.serializers.get(dataObject.getClass()).encode(dataObject, cacheArea.getPath(cacheDir, subCacheArea));
		} catch (IOException e) {
			cacheArea.clear(cacheDir);
			caches.remove(cacheArea.name);
			throw new RuntimeException(e);
		}
	}
}
