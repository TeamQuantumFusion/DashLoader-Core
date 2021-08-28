package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.Pointer;
import net.oskarstrom.dashloader.api.registry.storage.RegistryStorage;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class DashRegistryImpl implements DashRegistry {
	private final Object2ByteMap<Class<?>> storageMappings = new Object2ByteOpenHashMap<>();
	private final ArrayList<RegistryStorage<?>> storages;
	private final BiFunction<Object, DashRegistry, Integer> failedFunc;


	public DashRegistryImpl(BiFunction<Object, DashRegistry, Integer> failedFunc) {
		this.failedFunc = failedFunc;
		this.storages = new ArrayList<>();
	}

	public DashRegistryImpl(int size) {
		this.storages = new ArrayList<>(size);
		this.failedFunc = (o, r) -> {
			throw new UnsupportedOperationException("Called add on Deserialization registry");
		};
	}

	@Override
	public <F> int add(F object) {
		final Class<?> objectClass = object.getClass();
		if (!storageMappings.containsKey(objectClass)) {
			return failedFunc.apply(object, this);
		}
		final byte registryPointer = storageMappings.getByte(objectClass);
		//noinspection unchecked
		final RegistryStorage<F> registryStorage = (RegistryStorage<F>) storages.get(registryPointer);
		final int objectPointer = registryStorage.add(object);
		return Pointer.parsePointer(objectPointer, registryPointer);
	}

	public byte addStorage(RegistryStorage<?> registryStorage) {
		byte pos = (byte) storages.size();
		storages.add(registryStorage);
		return pos;
	}

	public RegistryStorage<?> getStorage(byte registryPointer) {
		return storages.get(registryPointer);
	}

	public void addMapping(Class<?> clazz, byte registryPointer) {
		storageMappings.put(clazz, registryPointer);
	}

	@Override
	public int getSize() {
		return 0;
	}

}
