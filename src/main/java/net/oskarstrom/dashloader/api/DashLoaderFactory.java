package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.serializer.DashSerializerManager;
import net.oskarstrom.dashloader.core.registry.DashRegistryImpl;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManagerImpl;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class DashLoaderFactory {

	public static DashRegistry createSerializationRegistry(BiFunction<Object, DashRegistry, Integer> failedFunc) {
		return new DashRegistryImpl(failedFunc);
	}

	public static DashRegistry createDeserializationRegistry(int size) {
		return new DashRegistryImpl(size);
	}


	public static DashSerializerManager createSerializationManager(Path systemCacheFolder) {
		return new DashSerializerManagerImpl(systemCacheFolder);
	}


}
