package net.oskarstrom.dashloader.core.registry.chunk.data;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.chunk.AbstractRegistryChunk;

/**
 * Holds the dashables for serialization
 *
 * @param <R> Target
 * @param <D> Dashables
 */
@SuppressWarnings("unused")
public interface RegistryChunkData<R, D extends Dashable<R>> {
	AbstractRegistryChunk<R, D> export();
}
