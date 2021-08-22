package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.oskarstrom.dashloader.api.registry.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashRegistryImpl implements DashRegistry {
	private final Object2ByteMap<Class<?>> storageMappings = new Object2ByteOpenHashMap<>();
	private final List<RegistryStorage<?>> storages;
	private final Set<Class<?>> apiFailed = new HashSet<>();


	public DashRegistryImpl() {
		this.storages = new ArrayList<>();
	}

	public DashRegistryImpl(int size) {
		this.storages = new ArrayList<>(size);
	}

	@Override
	public <F> Pointer add(F object) {
		final Class<?> objectClass = object.getClass();
		if (!storageMappings.containsKey(objectClass)) {
			apiFailed.add(objectClass);
			return null;
		}
		final byte registryPointer = storageMappings.getByte(objectClass);
		//noinspection unchecked
		final RegistryStorage<F> registryStorage = (RegistryStorage<F>) storages.get(registryPointer);
		final int objectPointer = registryStorage.add(object);
		return new Pointer(objectPointer, registryPointer);
	}

	public byte addStorage(RegistryStorage<?> registryStorage) {
		final byte pos = (byte) storages.size();
		storages.add(registryStorage);
		return pos;
	}

	public void addStorage(RegistryStorageData<?> registryStorageData) {
		storages.add(registryStorageData.registryPos, RegistryStorageFactory.createSupplierRegistry(this, registryStorageData.dashables));
	}

	public RegistryStorageData<?> getStorageData(byte registryPointer) {
		return new RegistryStorageData<>(storages.get(registryPointer).getDashables(), registryPointer);
	}

	public void addMapping(Class<?> clazz, byte registryPointer) {
		storageMappings.put(clazz, registryPointer);
	}

	@Override
	public int getSize() {
		return storages.size();
	}


	@Override
	public <F> F get(Pointer pointer) {
		final RegistryStorage<?> registryStorage = storages.get(pointer.registryPointer);
		if (registryStorage == null) {
			throw new IllegalStateException("Registry storage " + pointer.registryPointer + " does not exist.");
		}
		//noinspection unchecked
		return (F) registryStorage.get(pointer.objectPointer);
	}

	@Override
	public void apply(DashRegistry registry) {
		for (var storage : storages)
			storage.toUndash(registry);
	}
}
