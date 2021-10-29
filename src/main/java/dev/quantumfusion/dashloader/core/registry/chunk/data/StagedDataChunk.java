package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import dev.quantumfusion.dashloader.core.util.DashableEntry;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
public class StagedDataChunk<R, D extends Dashable<R>> extends AbstractDataChunk<R, D> {
	public final DashableEntry<D>[][] dashables;
	public final int dashablesSize;

	public StagedDataChunk(byte pos, DashableEntry<D>[][] dashables, int dashablesSize) {
		super(pos);
		this.dashables = dashables;
		this.dashablesSize = dashablesSize;
	}

	@Override
	public void export(Object[] data, DashRegistryReader registry) {
		for (DashableEntry<D>[] dashable : dashables)
			DashThreading.export(dashable, data, registry);
	}

	@Override
	public int getSize() {
		return dashablesSize;
	}
}
