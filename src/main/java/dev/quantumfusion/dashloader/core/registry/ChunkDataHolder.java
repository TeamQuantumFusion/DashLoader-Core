package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;

import java.util.Collection;

public interface ChunkDataHolder {

	Collection<AbstractDataChunk<?, ?>> getChunks();
}
