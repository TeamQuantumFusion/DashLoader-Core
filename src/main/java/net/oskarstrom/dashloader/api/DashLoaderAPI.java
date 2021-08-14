package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.RegistryStorageManager;
import net.oskarstrom.dashloader.api.serializer.DashSerializerManager;

public interface DashLoaderAPI {
	RegistryStorageManager getStorageManager();

	DashSerializerManager getSerializerManager();

	DashRegistry getRegistry();
}
