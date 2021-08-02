package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.FactoryConstructor;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;
import org.jetbrains.annotations.NotNull;

public class RegistryStorageManager {


	public static <F, D extends Dashable<F>> RegistryStorage<F> createSimpleRegistry(Class<F> rawClass, Class<D> dashClass, DashRegistry registry) {
		return new RegistryStorageImpl.SimpleRegistryImpl<>(getConstructor(rawClass, dashClass), registry);
	}

	public static <F, D extends Dashable<F>> RegistryStorage<F> createMultiRegistry(Object2ObjectMap<Class<F>, Class<D>> classes, DashRegistry registry) {
		Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructors = new Object2ObjectOpenHashMap<>((int) (classes.size() / 0.75f));
		for (var rawDashEntry : classes.entrySet()) {
			constructors.put(rawDashEntry.getKey(), getConstructor(rawDashEntry.getKey(), rawDashEntry.getValue()));
		}
		return new RegistryStorageImpl.FactoryRegistryImpl<>(constructors, registry);
	}

	@NotNull
	private static <F, D extends Dashable<F>> FactoryConstructor<F, D> getConstructor(Class<F> rawClass, Class<D> dashClass) {
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
