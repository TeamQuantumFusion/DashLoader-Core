package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.util.DashThreading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ThreadedChunkWriter<R, D extends Dashable<R>> extends ChunkWriter<R, D> {
	private final Class<R> targetClass;
	private final DashConstructor<R, D> constructor;
	private final Class<?> dashType;
	private final List<R> rawList = new ArrayList<>();


	public ThreadedChunkWriter(byte pos, DashRegistryWriter registry, Class<R> targetClass, DashConstructor<R, D> constructor, Class<?> dashType) {
		super(pos, registry);
		this.targetClass = targetClass;
		this.constructor = constructor;
		this.dashType = dashType;
	}

	@Override
	public int add(R object) {
		final int pos = rawList.size();
		rawList.add(object);
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
		D[] out = (D[]) new Dashable[rawList.size()];
		final String name = dashType.getSimpleName();
		DashThreading.writeTask("Exporting " + name, out, rawList.toArray(Object[]::new), constructor, registry);
		return new DataChunk<>(pos, name, out);
	}
}
