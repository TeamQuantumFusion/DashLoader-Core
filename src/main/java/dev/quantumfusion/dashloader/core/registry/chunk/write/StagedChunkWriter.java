package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.StagedDataChunk;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StagedChunkWriter<R, D extends Dashable<R>> extends ChunkWriter<R, D> {
	private final Object2ObjectMap<Class<?>, StageInfo<R, D>> mappings;
	private final List<DashThreading.DashableEntry<D>>[] dashableList;
	private int objectPos = 0;

	public StagedChunkWriter(byte pos, DashRegistryWriter registry, int stages, Object2ObjectMap<Class<?>, StageInfo<R, D>> mappings) {
		super(pos, registry);
		this.mappings = mappings;
		//noinspection unchecked
		this.dashableList = new List[stages];
		for (int i = 0; i < this.dashableList.length; i++)
			dashableList[i] = new ArrayList<>();
	}

	@Override
	public int add(R object) {
		final StageInfo<R, D> stageInfo = mappings.get(object.getClass());
		final D dashObject = stageInfo.constructor.invoke(object, registry);
		dashableList[stageInfo.stage].add(new DashThreading.DashableEntry<>(objectPos, dashObject));
		return objectPos++;
	}

	@Override
	public Collection<Class<?>> getClasses() {
		return mappings.keySet();
	}

	@Override
	public Collection<Class<?>> getDashClasses() {
		return mappings.values().stream().map(constructor -> constructor.constructor.dashClass).collect(Collectors.toList());
	}

	@Override
	@SuppressWarnings("unchecked")
	public AbstractDataChunk<R, D> exportData() {
		var out = new DashThreading.DashableEntry[dashableList.length][];

		for (int i = 0; i < dashableList.length; i++)
			out[i] = dashableList[i].toArray(DashThreading.DashableEntry[]::new);

		return new StagedDataChunk<R, D>(pos, out, objectPos);
	}


	public record StageInfo<R, D extends Dashable<R>>(DashConstructor<R, D> constructor, int stage) {
	}

}
