package net.oskarstrom.dashloader.api.registry.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.ThreadManager;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.FactoryConstructor;
import net.oskarstrom.dashloader.api.registry.export.ExportData;
import net.oskarstrom.dashloader.api.registry.export.MultiStageExportData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiStageRegistryStorage<F, D extends Dashable<F>> implements RegistryStorage<F> {
	public final int priority;
	private final Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor;
	private final Object2IntMap<F> deduplicationMap = new Object2IntOpenHashMap<>();
	private final DashRegistry registry;
	private final Map<Class<?>, Integer> stages;
	private final List<D> dashables;

	public MultiStageRegistryStorage(Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor, DashRegistry registry, int priority, Map<Class<?>, Integer> stages) {
		this.registry = registry;
		this.stages = stages;
		this.dashables = new ArrayList<>();
		this.constructor = constructor;
		this.priority = priority;
	}

	@Override
	public int add(F object) {
		if (deduplicationMap.containsKey(object))
			return deduplicationMap.getInt(object);

		final int pos = dashables.size();
		final D d = create(object, registry);
		dashables.add(d);
		deduplicationMap.put(object, pos);
		return pos;
	}

	public D create(F object, DashRegistry registry) {
		final FactoryConstructor<F, D> fdFactoryConstructor = constructor.get(object.getClass());
		if (fdFactoryConstructor == null) {
			//TODO error handling
			throw new IllegalStateException();
		}
		return fdFactoryConstructor.create(object, registry);
	}

	@Override
	public ExportData getExportData(byte registryPos) {
		List<List<ThreadManager.PosEntry<D>>> out = new ArrayList<>();
		stages.forEach((c, i) -> out.add(new ArrayList<>()));
		for (int i = 0, dashablesSize = dashables.size(); i < dashablesSize; i++) {
			D dashable = dashables.get(i);
			out.get(stages.get(dashable.getClass())).add(new ThreadManager.PosEntry<>(i, dashable));
		}
		//noinspection unchecked
		ThreadManager.PosEntry<D>[][] array = (ThreadManager.PosEntry<D>[][]) new ThreadManager.PosEntry[out.size()][];
		for (int j = 0; j < out.size(); j++) {
			//noinspection unchecked
			array[j] = (ThreadManager.PosEntry<D>[]) out.get(j).toArray(ThreadManager.PosEntry[]::new);
		}
		return new MultiStageExportData<>(array, registryPos, priority);
	}
}
