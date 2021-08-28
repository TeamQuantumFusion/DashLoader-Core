package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.storage.RegistryStorage;

import java.util.ArrayList;

public abstract class RegistryStorageImpl<F, D extends Dashable<F>> implements RegistryStorage<F> {
	private final Object2IntMap<F> deduplicationMap = new Object2IntOpenHashMap<>();
	private final DashRegistry registry;
	private final ArrayList<D> dashables;

	public RegistryStorageImpl(DashRegistry registry) {
		this.registry = registry;
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

	public D[] getDashables() {
		//noinspection unchecked
		return (D[]) dashables.toArray(Dashable[]::new);
	}


}
