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

	public <FI, DI extends Dashable<FI>, F extends FI, D extends DI> RegistryStorage<FI> createMultiRegistry(DashRegistry registry, List<Map.Entry<Class<F>, Class<D>>> classes) {
		Object2ObjectMap<Class<FI>, FactoryConstructor<FI, DI>> constructors = new Object2ObjectOpenHashMap<>((int) (classes.size() / 0.75f));
		for (var rawDashEntry : classes) {
			//noinspection unchecked
			constructors.put((Class<FI>) rawDashEntry.getKey(), getConstructor(rawDashEntry.getKey(), rawDashEntry.getValue()));
		}
		return new RegistryStorageImpl.FactoryRegistryImpl<>(constructors, registry);
	}

	@NotNull
	private <FI, DI extends Dashable<FI>, F extends FI, D extends DI> FactoryConstructor<FI, DI> getConstructor(Class<F> rawClass, Class<D> dashClass) {
		try {
			return FactoryConstructorImpl.createConstructor(rawClass, dashClass);
			//TODO error handling
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}


}
