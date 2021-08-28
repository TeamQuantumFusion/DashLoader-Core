package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.RegistryStorage;

public abstract class RegistryStorageImpl<F, D extends Dashable<F>> implements RegistryStorage<F> {
	private final Object2IntMap<F> deduplicationMap = new Object2IntOpenHashMap<>();
	private final DashRegistry registry;
	private final F[] unDashedObjects; // bound to F
	private D[] dashables; // bound to D
	private int pos = 0;

	public RegistryStorageImpl(DashRegistry registry) {
		this.registry = registry;
		this.unDashedObjects = null;
		//noinspection unchecked
		this.dashables = (D[]) new Dashable[1];
	}

	public RegistryStorageImpl(DashRegistry registry, D[] dashables) {
		this.registry = registry;
		this.dashables = dashables;
		//noinspection unchecked
		this.unDashedObjects = (F[]) new Object[dashables.length];
	}

	@Override
	public int add(F object) {
		if (deduplicationMap.containsKey(object))
			return deduplicationMap.getInt(object);
		final D dashObject = create(object, registry);
		ensureDashableSize(pos + 1);
		int pos = this.pos;
		dashables[pos] = dashObject;
		deduplicationMap.put(object, pos);
		this.pos++;
		return pos;
	}

	private void ensureDashableSize(int size) {
		if (dashables.length < size) {
			//noinspection unchecked
			D[] newArray = (D[]) new Dashable[dashables.length * 2];
			System.arraycopy(dashables, 0, newArray, 0, dashables.length);
			dashables = newArray;
		}
	}

	public abstract D create(F object, DashRegistry registry);

	@SuppressWarnings("unchecked")
	public D[] getDashables() {
		D[] trimmedArray = (D[]) new Dashable[pos];
		System.arraycopy(dashables, 0, trimmedArray, 0, pos);
		return trimmedArray;
	}


}
