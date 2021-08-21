package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

import java.util.Collection;
import java.util.Map;

public interface RegistryStorageManager {
	<F, D extends Dashable<F>> RegistryStorage<F> createSimpleRegistry(DashRegistry registry, Class<F> rawClass, Class<D> dashClass);

	<F, D extends Dashable<F>> RegistryStorage<F> createSupplierRegistry(DashRegistry registry, D[] data);

	<F, D extends Dashable<F>> RegistryStorage<F> createMultiRegistry(DashRegistry registry, Collection<Map.Entry<Class<? extends F>, Class<? extends D>>> classes);
}
