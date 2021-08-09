package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.oskarstrom.dashloader.api.data.PairMap;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.Pointer;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DashRegistryImpl implements DashRegistry {
	private final PairMap<Byte, Consumer<RegistryStorage<?>>> registryReturns = new PairMap<>();
	private final Object2ByteMap<Class<?>> storageMappings = new Object2ByteOpenHashMap<>();
	private final List<RegistryStorage<?>> storages;
	private final Set<Class<?>> apiFailed = new HashSet<>();


	public DashRegistryImpl() {
		this.storages = new ArrayList<>();
	}

	public DashRegistryImpl(List<RegistryStorage<?>> storages) {
		this.storages = storages;
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

	public void addMapping(Class<?> clazz, byte registryPointer) {
		storageMappings.put(clazz, registryPointer);
	}


	public void addReturn(byte registryPointer, Consumer<RegistryStorage<?>> func) {
		registryReturns.add(PairMap.Entry.of(registryPointer, func));
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

		for (var registryReturn : registryReturns)
			registryReturn.value.accept(storages.get(registryReturn.key));

	}
}
