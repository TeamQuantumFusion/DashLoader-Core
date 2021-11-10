package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.DashLoaderCore;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;

public class DataChunk<R, D extends Dashable<R>> extends AbstractDataChunk<R, D> {
	private final D[] dashables;

	public DataChunk(byte pos, String name, D[] dashables) {
		super(pos, name);
		this.dashables = dashables;
	}

	@Override
	public void preExport(RegistryReader reader) {
		for (D dashable : dashables) dashable.preExport(reader);
	}

	@Override
	public void export(Object[] data, RegistryReader registry) {
		DashLoaderCore.CORE.getThreadHandler().parallelExport(dashables, data, registry);
	}

	@Override
	public void postExport(RegistryReader reader) {
		for (D dashable : dashables) dashable.postExport(reader);
	}

	@Override
	public int getDashableSize() {
		return dashables.length;
	}
}
