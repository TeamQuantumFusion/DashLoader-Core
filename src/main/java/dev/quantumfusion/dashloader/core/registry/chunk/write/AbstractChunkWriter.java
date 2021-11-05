package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.registry.WriteFailCallback;
import dev.quantumfusion.dashloader.core.registry.chunk.AbstractChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;

import java.util.Collection;

/**
 * Holds and handles the target objects
 */
@SuppressWarnings("unused")
public abstract class AbstractChunkWriter<R, D extends Dashable<R>> extends AbstractChunk {
	public final Class<?> dashType;
	protected final DashRegistryWriter writer;
	protected final Creator<R, D> callback;

	protected AbstractChunkWriter(byte pos, DashRegistryWriter writer, WriteFailCallback<R, D> callback, Class<?> dashType) {
		super(pos);
		this.writer = writer;
		this.callback = new Creator<>(callback, this);
		this.dashType = dashType;
	}

	public abstract int add(R object);

	public abstract Collection<Class<?>> getClasses();

	public abstract Collection<Class<?>> getDashClasses();

	public AbstractDataChunk<R, D> exportData() {
		final String dashName = dashType.getSimpleName();
		final D[] dashOut = writeOut("Exporting: " + dashName);
		return new DataChunk<>(pos, dashName, dashOut);
	}

	public abstract D[] writeOut(String taskName);
}
