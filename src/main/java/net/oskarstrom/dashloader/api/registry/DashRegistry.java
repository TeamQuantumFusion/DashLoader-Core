package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Applyable;

public interface DashRegistry extends Applyable {
	<F> Pointer add(F object);

	<F> F get(Pointer pointer);

	byte addStorage(RegistryStorage<?> registryStorage);

	void addStorage(RegistryStorageData<?> registryStorage);

	RegistryStorageData<?> getStorageData(byte registryPointer);

	RegistryStorage<?> getStorage(byte registryPointer);

	void addMapping(Class<?> clazz, byte registryPointer);

	int getSize();
}
