package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.Pointer;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashRegistryImpl implements DashRegistry {
	private Object2ByteMap<Class<?>> storageMappings = new Object2ByteOpenHashMap<>();
	private List<RegistryStorage<?>> storages = new ArrayList<>();
	private Set<Class<?>> apiFailed = new HashSet<>();

	@Override
	public <F> Pointer add(F object) {
		final Class<?> objectClass = object.getClass();
		if (!storageMappings.containsKey(objectClass)) {
			apiFailed.add(objectClass);
			return null;
		}
		final byte registryPointer = storageMappings.getByte(objectClass);
		final RegistryStorage<F> registryStorage = (RegistryStorage<F>) storages.get(registryPointer);
		final int objectPointer = registryStorage.add(object);
		return new Pointer(objectPointer, registryPointer);
	}

	public byte addStorage(RegistryStorage<?> registryStorage) {
		final byte pos = (byte) storages.size();
		storages.add(registryStorage);
		return pos;
	}

	public void addMapping(Class<?> clazz, byte registryPointer) {
		storageMappings.put(clazz, registryPointer);
	}

	@Override
	public <F> F get(Pointer pointer) {
		final RegistryStorage<?> registryStorage = storages.get(pointer.registryPointer);
		if (registryStorage == null) {
			throw new IllegalStateException("Registry storage " + pointer.registryPointer + " does not exist.");
		}
		return (F) registryStorage.get(pointer.objectPointer);
	}
}
