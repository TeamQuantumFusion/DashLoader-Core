package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;

public interface ChunkHolder {
	AbstractDataChunk<?, ?>[] getChunks();
}
