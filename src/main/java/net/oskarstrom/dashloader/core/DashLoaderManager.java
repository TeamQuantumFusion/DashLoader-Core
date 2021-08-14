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


}
