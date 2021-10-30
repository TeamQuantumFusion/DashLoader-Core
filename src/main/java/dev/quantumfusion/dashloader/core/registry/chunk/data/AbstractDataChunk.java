package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;
import dev.quantumfusion.hyphen.scan.annotations.Data;

/**
 * Holds the dashables for serialization
 *
 * @param <R> Target
 * @param <D> Dashables
 */
@SuppressWarnings("unused")
@Data
public abstract class AbstractDataChunk<R, D extends Dashable<R>> extends AbstractChunk {
	public final String name;

	public AbstractDataChunk(byte pos, String name) {
		super(pos);
		this.name = name;
	}

	public abstract void prepare(DashRegistryReader registry);

	public abstract void export(Object[] data, DashRegistryReader registry);

	public abstract void apply(DashRegistryReader registry);

	public abstract int getSize();
}
