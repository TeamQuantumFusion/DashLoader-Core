package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.factory.DashFactory;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractWriteChunk<R, D extends Dashable<R>> extends AbstractChunk<R, D> {
	protected final RegistryWriter writer;
	protected final DashFactory<R, D> factory;
	protected final Collection<DashObjectClass<R, D>> dashObjects;

	protected AbstractWriteChunk(byte pos, String name, RegistryWriter writer, DashFactory<R, D> factory, Collection<DashObjectClass<R, D>> dashObjects) {
		super(pos, name);
		this.writer = writer;
		this.factory = factory;
		this.dashObjects = dashObjects;
	}

	public abstract int add(R raw);

	public Collection<Class<?>> getTargetClasses() {
		return dashObjects.stream().map(DashObjectClass::getTargetClass).collect(Collectors.toList());
	}

	public abstract AbstractDataChunk<R, D> exportData();
}
