package net.oskarstrom.dashloader.api.registry;

public interface DashRegistry {

	<F> int add(F object);

	byte addStorage(RegistryStorage<?> registryStorage);


	RegistryStorage<?> getStorage(byte registryPointer);

	void addMapping(Class<?> clazz, byte registryPointer);

	int getSize();
}
