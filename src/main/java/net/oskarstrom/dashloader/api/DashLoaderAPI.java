package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;
import net.oskarstrom.dashloader.api.registry.RegistryStorageManager;
import net.oskarstrom.dashloader.api.serializer.DashSerializerManager;

import java.util.List;
import java.util.function.Consumer;

public interface DashLoaderAPI {
	RegistryStorageManager getStorageManager();

	<F> void registerRegistry(List<Class<F>> mappings, RegistryStorage<F> storage, Consumer<RegistryStorage<?>> consumer);

	DashSerializerManager getSerializerManager();

	DashRegistry getRegistry();
}
