package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

import java.util.List;
import java.util.Map;

public interface RegistryStorageManager {
	<F, D extends Dashable<F>> RegistryStorage<F> createSimpleRegistry(DashRegistry registry, Class<F> rawClass, Class<D> dashClass);

	<F, D extends Dashable<F>> RegistryStorage<F> createSupplierRegistry(DashRegistry registry, D[] data);

	<FI, DI extends Dashable<FI>, F extends FI, D extends DI> RegistryStorage<FI> createMultiRegistry(DashRegistry registry, List<Map.Entry<Class<F>, Class<D>>> classes);
}
