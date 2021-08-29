package net.oskarstrom.dashloader.core.registry.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.DashRegistry;

import java.util.ArrayList;
import java.util.List;

public abstract class RegistryStorageImpl<F, D extends Dashable<F>> implements RegistryStorage<F> {
	private final Object2IntMap<F> deduplicationMap = new Object2IntOpenHashMap<>();
	protected final DashRegistry registry;
	protected final List<D> dashables;
	protected final Class<?> tag;

	public RegistryStorageImpl(DashRegistry registry, Class<?> tag) {
		this.registry = registry;
		this.tag = tag;
		this.dashables = new ArrayList<>();
	}

	@Override
	public int add(F object) {
		if (deduplicationMap.containsKey(object))
			return deduplicationMap.getInt(object);

		final int pos = dashables.size();
		dashables.add(create(object, registry));
		deduplicationMap.put(object, pos);
		return pos;
	}

	public abstract D create(F object, DashRegistry registry);



}
