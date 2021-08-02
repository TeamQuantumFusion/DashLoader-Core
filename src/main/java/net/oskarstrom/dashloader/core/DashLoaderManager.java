package net.oskarstrom.dashloader.core;

import net.oskarstrom.dashloader.api.DashLoaderAPI;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;
import net.oskarstrom.dashloader.core.registry.DashRegistryImpl;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManager;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class DashLoaderManager implements DashLoaderAPI {
	private final Path dataCacheFolder;
	private final Path systemCacheFolder;

	private final DashRegistryImpl registry = new DashRegistryImpl();
	private final DashSerializerManager serializerManager = new DashSerializerManager(this);

	public DashLoaderManager(Path dataCacheFolder, Path systemCacheFolder) {
		this.dataCacheFolder = dataCacheFolder;
		this.systemCacheFolder = systemCacheFolder;
	}

	public Path getDataCacheFolder() {
		return dataCacheFolder;
	}

	public Path getSystemCacheFolder() {
		return systemCacheFolder;
	}

	/**
	 * Use this to register a custom registry for deduplication of objects.
	 *
	 * @param mappings A list of classes that will be redirected to a Registries {@link RegistryStorage#add(Object)}
	 * @param storage  The {@link RegistryStorage} you are registering
	 * @param consumer After the caching step has been complete this will be run (used mostly for putting it in a data object and serializing it)
	 * @param <F>      The type of classes the registry holds.
	 */
	@Override
	public <F> void registerRegistry(List<Class<F>> mappings, RegistryStorage<F> storage, Consumer<RegistryStorage<?>> consumer) {
		final byte pointer = registry.addStorage(storage);
		mappings.forEach(fClass -> registry.addMapping(fClass, pointer));
		registry.addReturn(pointer, consumer);
	}
}
