package net.oskarstrom.dashloader.core.registry.chunk;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.chunk.data.RegistryChunkData;

import java.util.List;

/**
 * Holds and handles the target objects
 */
@SuppressWarnings("unused")
public abstract class AbstractRegistryChunk<R, D extends Dashable<R>> {
	public final byte pos;

	protected AbstractRegistryChunk(byte pos) {
		this.pos = pos;
	}

	public abstract int add(R object);

	public abstract R get(int id);

	public abstract List<Class<?>> getClasses();

	public abstract RegistryChunkData<R, D> exportData();

}
