package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.oskarstrom.dashloader.core.registry.storage.RegistryStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class DashRegistry {
	private final Object2ObjectMap<ExplicitMatcher, Class<?>> explicitMappings;
	private final BiFunction<Object, DashRegistry, Integer> failedFunc;
	private final Object2ByteMap<Class<?>> storageMappings;
	private final Object2ByteMap<Class<?>> tags;
	private final List<RegistryStorage<?>> storages;


	public DashRegistry(Object2ByteMap<Class<?>> storageMappings,
						Object2ObjectMap<ExplicitMatcher, Class<?>> explicitMappings,
						BiFunction<Object, DashRegistry, Integer> failedFunc, Object2ByteMap<Class<?>> tags) {
		this.storageMappings = storageMappings;
		this.explicitMappings = explicitMappings;
		this.failedFunc = failedFunc;
		this.tags = tags;
		this.storages = new ArrayList<>();
	}


	public <F> int add(F object) {
		final Class<?> objectClass = object.getClass();
		if (!storageMappings.containsKey(objectClass)) {
			for (var matcherPointerEntry : explicitMappings.object2ObjectEntrySet()) {
				final boolean testResult = matcherPointerEntry.getKey().test(object, this, storageMappings);
				if (testResult) {
					if (storageMappings.containsKey(matcherPointerEntry.getValue())) {
						final byte storagePointer = storageMappings.getByte(matcherPointerEntry.getValue());
						return addObjectToStorage(object, storagePointer);
					}
					break;
				}
			}
			return failedFunc.apply(object, this);
		}
		return addObjectToStorage(object, storageMappings.getByte(objectClass));
	}


	private <F> int addObjectToStorage(F object, byte registryPointer) {
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


	public <D> RegistryStorage<D> getStorage(Class<D> tag) {
		//noinspection unchecked
		return (RegistryStorage<D>) getStorage(getStorageId(tag));
	}

	public <D> byte getStorageId(Class<D> tag) {
		return tags.getByte(tag);
	}


	public void addMapping(Class<?> clazz, byte registryPointer) {
		storageMappings.put(clazz, registryPointer);
	}

	public int getSize() {
		return 0;
	}

	@FunctionalInterface
	public interface ExplicitMatcher {
		<F> boolean test(F object, DashRegistry dashRegistry, Object2ByteMap<Class<?>> mappings);
	}
}
