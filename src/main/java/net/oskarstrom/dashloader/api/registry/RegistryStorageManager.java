package net.oskarstrom.dashloader.api.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.oskarstrom.dashloader.api.Dashable;

public interface RegistryStorageManager {
	<F, D extends Dashable<F>> RegistryStorage<F> createSimpleRegistry(Class<F> rawClass, Class<D> dashClass, DashRegistry registry);

	<F, D extends Dashable<F>> RegistryStorage<F> createSupplierRegistry(D[] data, DashRegistry registry);

	<F, D extends Dashable<F>> RegistryStorage<F> createMultiRegistry(Object2ObjectMap<Class<F>, Class<D>> classes, DashRegistry registry);
}
