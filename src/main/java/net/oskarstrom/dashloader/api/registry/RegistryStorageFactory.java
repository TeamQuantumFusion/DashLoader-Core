package net.oskarstrom.dashloader.api.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.core.registry.FactoryConstructorImpl;
import net.oskarstrom.dashloader.core.registry.RegistryStorageImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class RegistryStorageFactory {


	public static <F, D extends Dashable<F>> RegistryStorage<F> createSimpleRegistry(DashRegistry registry, Class<F> rawClass, Class<D> dashClass) {
		return new SimpleRegistryImpl<>(getConstructor(rawClass, dashClass), registry);
	}


	public static <F, D extends Dashable<F>> RegistryStorage<F> createSupplierRegistry(DashRegistry registry, D[] data) {
		return new SupplierRegistryImpl<>(registry, data);
	}

	public static <F, D extends Dashable<F>> RegistryStorage<F> createMultiRegistry(DashRegistry registry, Collection<Map.Entry<Class<? extends F>, Class<? extends D>>> classes) {
		Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructors = new Object2ObjectOpenHashMap<>((int) (classes.size() / 0.75f));
		for (var rawDashEntry : classes) {
			//noinspection unchecked
			constructors.put((Class<F>) rawDashEntry.getKey(), getConstructor(rawDashEntry.getKey(), rawDashEntry.getValue()));
		}
		return new FactoryRegistryImpl<>(constructors, registry);
	}

	@NotNull
	private static <F, D extends Dashable<F>> FactoryConstructor<F, D> getConstructor(Class<? extends F> rawClass, Class<? extends D> dashClass) {
		try {
			return FactoryConstructorImpl.createConstructor(rawClass, dashClass);
			//TODO error handling
		} catch (IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	static class SimpleRegistryImpl<F, D extends Dashable<F>> extends RegistryStorageImpl<F, D> {
		private final FactoryConstructor<F, D> constructor;

		public SimpleRegistryImpl(FactoryConstructor<F, D> constructor, DashRegistry registry) {
			super(registry);
			this.constructor = constructor;
		}

		@Override
		public D create(F object, DashRegistry registry) {
			return constructor.create(object, registry);
		}
	}

	static class SupplierRegistryImpl<F, D extends Dashable<F>> extends RegistryStorageImpl<F, D> {

		public SupplierRegistryImpl(DashRegistry registry, D[] data) {
			super(registry, data);
		}

		@Override
		public D create(F object, DashRegistry registry) {
			throw new UnsupportedOperationException("This registry is purely for Deserialization");
		}
	}

	static class FactoryRegistryImpl<F, D extends Dashable<F>> extends RegistryStorageImpl<F, D> {
		private final Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor;

		public FactoryRegistryImpl(Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor, DashRegistry registry) {
			super(registry);
			this.constructor = constructor;
		}

		@Override
		public D create(F object, DashRegistry registry) {
			final FactoryConstructor<F, D> fdFactoryConstructor = constructor.get(object);
			if (fdFactoryConstructor == null) {
				//TODO error handling
				throw new IllegalStateException();
			}
			return fdFactoryConstructor.create(object, registry);
		}
	}


}
