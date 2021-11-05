package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractSingleChunkWriter<L, R, D extends Dashable<R>> extends AbstractChunkWriter<R, D> {
	protected final Creator<R, D> constructor;
	protected final Class<R> targetClass;
	protected final List<L> list = new ArrayList<>();

	protected AbstractSingleChunkWriter(byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, Class<D> dashType, Class<R> targetClass) {
		super(pos, writer, callback, dashType);
		this.constructor = new Creator<>(callback, DashConstructor.create(dashType, targetClass), this);
		this.targetClass = targetClass;
	}

	@Override
	public int add(R object) {
		final int pos = list.size();
		list.add(computeListItem(object));
		return pos;
	}

	@Override
	public D[] writeOut(String taskName) {
		return writeOut(list, taskName);
	}

	public abstract L computeListItem(R object);

	public abstract D[] writeOut(List<L> list, String taskName);

	@Override
	public Collection<Class<?>> getClasses() {
		return List.of(targetClass);
	}

	@Override
	public Collection<Class<?>> getDashClasses() {
		return List.of(dashType);
	}
}
