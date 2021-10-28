package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;

import java.util.Collection;

/**
 * Holds and handles the target objects
 */
@SuppressWarnings("unused")
public abstract class ChunkWriter<R, D extends Dashable<R>> extends AbstractChunk {
	protected final DashRegistryWriter registry;

	protected ChunkWriter(byte pos, DashRegistryWriter registry) {
		super(pos);
		this.registry = registry;
	}

	public abstract int add(R object);

	public abstract Collection<Class<?>> getClasses();

	public abstract AbstractDataChunk<R, D> exportData();

}
