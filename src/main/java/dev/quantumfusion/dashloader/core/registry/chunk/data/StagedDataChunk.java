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

	public StagedDataChunk(byte pos, String name, DashableEntry<D>[][] dashables, int dashablesSize) {
		super(pos, name);
		this.dashables = dashables;
		this.dashablesSize = dashablesSize;
	}

	@Override
	public void prepare(DashRegistryReader reader) {
		for (var stage : dashables)
			for (var entry : stage)
				entry.dashable().prepare(reader);
	}

	@Override
	public void export(Object[] data, DashRegistryReader registry) {
		for (DashableEntry<D>[] dashable : dashables)
			DashThreading.export(dashable, data, registry);
	}

	@Override
	public void apply(DashRegistryReader reader) {
		for (var stage : dashables)
			for (var entry : stage)
				entry.dashable().apply(reader);
	}

	@Override
	public int getSize() {
		return dashablesSize;
	}
}
