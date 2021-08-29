package net.oskarstrom.dashloader.core.registry.storage;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.ThreadManager;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.registry.FactoryConstructor;
import net.oskarstrom.dashloader.core.registry.export.ExportData;
import net.oskarstrom.dashloader.core.registry.export.MultiStageExportData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiStageRegistryStorage<F, D extends Dashable<F>> extends MultiRegistryStorage<F, D> {
	private final Map<Class<?>, Integer> stages;

	public MultiStageRegistryStorage(Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor, DashRegistry registry, Map<Class<?>, Integer> stages, Class<?> tag) {
		super(constructor, registry, tag);
		this.stages = stages;
	}


	@Override
	public ExportData<F, D> getExportData() {
		final byte storageId = registry.getStorageId(tag);
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
		return new MultiStageExportData<>(array, storageId, dashables.size());
	}


}
