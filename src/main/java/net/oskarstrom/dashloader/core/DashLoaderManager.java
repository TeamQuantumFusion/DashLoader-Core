package net.oskarstrom.dashloader.core;

import net.oskarstrom.dashloader.api.DashLoaderAPI;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;
import net.oskarstrom.dashloader.api.registry.RegistryStorageManager;
import net.oskarstrom.dashloader.api.serializer.DashSerializerManager;
import net.oskarstrom.dashloader.core.registry.DashRegistryImpl;
import net.oskarstrom.dashloader.core.registry.RegistryStorageManagerImpl;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManagerImpl;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class DashLoaderManager implements DashLoaderAPI {
	private static DashLoaderManager instance;
	private final Path systemCacheFolder;

	private final DashSerializerManager serializerManager = new DashSerializerManagerImpl(this);
	private final RegistryStorageManager storageManager = new RegistryStorageManagerImpl();
	private final DashRegistryImpl registry;
	private final ThreadManager threadManager = new ThreadManager();

	public DashLoaderManager(Path systemCacheFolder) {
		this.systemCacheFolder = systemCacheFolder;
		registry = new DashRegistryImpl();
		instance = this;
	}

	public DashLoaderManager(Path systemCacheFolder, List<RegistryStorage<?>> storages) {
		this.systemCacheFolder = systemCacheFolder;
		registry = new DashRegistryImpl(storages);
		instance = this;
	}

	public static DashLoaderManager getInstance() {
		return instance;
	}

	public Path getSystemCacheFolder() {
		return systemCacheFolder;
	}

	public ThreadManager getThreadManager() {
		return threadManager;
	}

	public RegistryStorageManager getStorageManager() {
		return storageManager;
	}

	public DashSerializerManager getSerializerManager() {
		return serializerManager;
	}

	public DashRegistry getRegistry() {
		return registry;
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
