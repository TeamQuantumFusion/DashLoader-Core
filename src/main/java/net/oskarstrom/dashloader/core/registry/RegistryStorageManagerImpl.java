package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.FactoryConstructor;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;
import net.oskarstrom.dashloader.api.registry.RegistryStorageManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RegistryStorageManagerImpl implements RegistryStorageManager {


	public <F, D extends Dashable<F>> RegistryStorage<F> createSimpleRegistry(DashRegistry registry, Class<F> rawClass, Class<D> dashClass) {
		return new RegistryStorageImpl.SimpleRegistryImpl<>(getConstructor(rawClass, dashClass), registry);
	}


	public <F, D extends Dashable<F>> RegistryStorage<F> createSupplierRegistry(DashRegistry registry, D[] data) {
		return new RegistryStorageImpl.SupplierRegistryImpl<>(registry, data);
	}

	public <F, D extends Dashable<F>> RegistryStorage<F> createMultiRegistry(DashRegistry registry, List<Map.Entry<Class<? extends F>, Class<? extends D>>> classes) {
		Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructors = new Object2ObjectOpenHashMap<>((int) (classes.size() / 0.75f));
		for (var rawDashEntry : classes) {
			//noinspection unchecked
			constructors.put((Class<F>) rawDashEntry.getKey(), getConstructor(rawDashEntry.getKey(), rawDashEntry.getValue()));
		}
		return new RegistryStorageImpl.FactoryRegistryImpl<>(constructors, registry);
	}

	@NotNull
	private <F, D extends Dashable<F>> FactoryConstructor<F, D> getConstructor(Class<? extends F> rawClass, Class<? extends D> dashClass) {
		try {
			return FactoryConstructorImpl.createConstructor(rawClass, dashClass);
			//TODO error handling
		} catch (IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}


}
