package net.oskarstrom.dashloader.core.registry.chunk;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.chunk.data.RegistryChunkData;

/**
 * Holds and handles the target objects
 */
@SuppressWarnings("unused")
public interface RegistryChunk<R, D extends Dashable<R>> {
	int add(R object);

	R get(int id);

	RegistryChunkData<R, D> exportData();

	void importData(RegistryChunkData<R, D> data);
}
