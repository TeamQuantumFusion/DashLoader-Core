package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;

/**
 * Holds the dashables for serialization
 *
 * @param <R> Target
 * @param <D> Dashables
 */
@SuppressWarnings("unused")
public abstract class AbstractDataChunk<R, D extends Dashable<R>> extends AbstractChunk {
	public AbstractDataChunk(byte pos) {
		super(pos);
	}

	public abstract void export(Object[] data, DashRegistryReader registry);

	public abstract int getSize();
}
