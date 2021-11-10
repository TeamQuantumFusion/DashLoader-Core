package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.DashLoaderCore;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;
import dev.quantumfusion.dashloader.core.thread.IndexedArrayMapTask;
import dev.quantumfusion.dashloader.core.thread.ThreadHandler;

public class StagedDataChunk<R, D extends Dashable<R>> extends AbstractDataChunk<R, D> {
	public final IndexedArrayMapTask.IndexedArrayEntry<D>[][] dashables;

	public final int dashablesSize;

	public StagedDataChunk(byte pos, String name, IndexedArrayMapTask.IndexedArrayEntry<D>[][] dashables, int dashablesSize) {
		super(pos, name);
		this.dashables = dashables;
		this.dashablesSize = dashablesSize;
	}

	@Override
	public void preExport(RegistryReader reader) {
		for (var stage : dashables) for (var entry : stage) entry.object().preExport(reader);

	}

	@Override
	public void export(Object[] data, RegistryReader registry) {
		final ThreadHandler threadHandler = DashLoaderCore.CORE.getThreadHandler();
		for (IndexedArrayMapTask.IndexedArrayEntry<D>[] dashable : dashables) {
			threadHandler.parallelExport(dashable, data, registry);
		}
	}

	@Override
	public void postExport(RegistryReader reader) {
		for (var stage : dashables) for (var entry : stage) entry.object().postExport(reader);

	}

	@Override
	public int getDashableSize() {
		return dashablesSize;
	}
}
