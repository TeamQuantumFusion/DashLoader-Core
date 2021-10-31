package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.ui.DashLoaderProgress;

import java.util.Collection;
import java.util.List;

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

	public abstract Collection<Class<?>> getDashClasses();

	public abstract AbstractDataChunk<R, D> exportData();


	protected DataChunk<R, D> export(Class<?> type, List<D> list) {
		final String name = type.getSimpleName();
		DashLoaderProgress.PROGRESS.setCurrentSubtask("Exporting " + name, list.size());
		D[] out = (D[]) new Dashable[list.size()];
		for (int i = 0; i < list.size(); i++) {
			out[i] = list.get(i);
			DashLoaderProgress.PROGRESS.completedSubTask();
		}
		return new DataChunk<>(pos, name, out);
	}
}
