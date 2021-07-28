package net.oskarstrom.dashloader.api.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;

import java.util.ArrayList;
import java.util.List;

public class RegistryStorageImpl<F, D extends Dashable<F>> implements RegistryStorage<F>, Dashable<List<F>> {
	private final List<F> unDashedObjects = new ArrayList<>();
	private final Object2IntMap<F> deduplicationMap = new Object2IntOpenHashMap<>();
	private final DashRegistry registry;
	private final List<D> dashables;
	private FactoryConstructor<F, D> constructor;

	public RegistryStorageImpl(FactoryConstructor<F, D> constructor, DashRegistry registry) {
		this.constructor = constructor;
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

		final D dashObject = constructor.create(object, registry);
		final int pos = dashables.size();
		dashables.add(dashObject);
		deduplicationMap.put(object, pos);
		return pos;
	}


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
}
