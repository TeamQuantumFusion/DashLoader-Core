package net.oskarstrom.dashloader.core;

import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManager;

import java.nio.file.Path;

public class DashLoaderFactory {

	public static DashExportHandler createExportHandler(int size) {
		return new DashExportHandler(size);
	}

	public static DashSerializerManager createSerializationManager(Path systemCacheFolder) {
		return new DashSerializerManager(systemCacheFolder);
	}


}
