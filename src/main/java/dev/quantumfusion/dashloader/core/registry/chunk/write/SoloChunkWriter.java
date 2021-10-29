package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoloChunkWriter<R, D extends Dashable<R>> extends ChunkWriter<R, D> {
	private final Class<R> targetClass;
	private final DashConstructor<R, D> constructor;

	private final List<D> dashableList = new ArrayList<>();


	public SoloChunkWriter(byte pos, DashRegistryWriter registry, Class<R> targetClass, DashConstructor<R, D> constructor) {
		super(pos, registry);
		this.targetClass = targetClass;
		this.constructor = constructor;
	}

	@Override
	public int add(R object) {
		final int pos = dashableList.size();
		dashableList.add(constructor.invoke(object, registry));
		return pos;
	}

	@Override
	public Collection<Class<?>> getClasses() {
		return List.of(targetClass);
	}

	@Override
	public Collection<Class<?>> getDashClasses() {
		return List.of(constructor.dashClass);
	}

	@Override
	public AbstractDataChunk<R, D> exportData() {
		return new DataChunk<>(pos, dashableList.toArray(Dashable[]::new));
	}
}
