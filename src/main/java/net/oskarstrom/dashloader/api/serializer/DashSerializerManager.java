package net.oskarstrom.dashloader.api.serializer;

import net.oskarstrom.dashloader.core.serializer.DashSerializerImpl;

import java.nio.file.Path;
import java.util.Collection;

public interface DashSerializerManager {
	<T> void loadOrCreateSerializer(String serializerName, Class<T> klazz, Path startingPath, String... keys);

	<T> DashSerializerImpl<T> getSerializer(Class<T> klazz);

	void addSubclass(String key, Class<?> klazz);

	void addSubclasses(String key, Collection<Class<?>> classes);
}
