package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;

public abstract class AbstractDataChunk<R, D extends Dashable<R>> extends AbstractChunk<R, D> {
	protected AbstractDataChunk(byte pos, String name) {
		super(pos, name);
	}

	public abstract void preExport(RegistryReader registry);

	public abstract void export(Object[] data, RegistryReader registry);

	public abstract void postExport(RegistryReader registry);

	public abstract int getDashableSize();
}
