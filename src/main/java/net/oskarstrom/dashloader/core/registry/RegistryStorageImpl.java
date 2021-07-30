package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.FactoryConstructor;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;

import java.util.ArrayList;
import java.util.List;

public abstract class RegistryStorageImpl<F, D extends Dashable<F>> implements RegistryStorage<F>, Dashable<List<F>> {
	private final List<F> unDashedObjects = new ArrayList<>();
	private final Object2IntMap<F> deduplicationMap = new Object2IntOpenHashMap<>();
	private final DashRegistry registry;
	private final List<D> dashables;

	public RegistryStorageImpl(DashRegistry registry) {
		this.registry = registry;
		this.dashables = new ArrayList<>();
	}

	public RegistryStorageImpl(List<D> dashables, DashRegistry registry) {
		this.registry = registry;
		this.dashables = dashables;
	}

	@Override
	public int add(F object) {
		if (deduplicationMap.containsKey(object))
			return deduplicationMap.getInt(object);

		final D dashObject = create(object, registry);
		final int pos = dashables.size();
		dashables.add(dashObject);
		deduplicationMap.put(object, pos);
		return pos;
	}

	public abstract D create(F object, DashRegistry registry);


	public List<D> getDashables() {
		return dashables;
	}

	@Override
	public F get(int pointer) {
		return unDashedObjects.get(pointer);
	}

	@Override
	public List<F> toUndash(DashRegistry registry) {
		if (dashables == null || dashables.isEmpty()) {
			throw new IllegalStateException("Dashables are not available.");
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
