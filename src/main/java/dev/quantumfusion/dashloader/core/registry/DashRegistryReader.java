package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;

@SuppressWarnings("FinalMethodInFinalClass")
public final class DashRegistryReader {
	private final DataChunk<?, ?>[] dataChunks;

	// Holds an array of the exported datachunks array values.
	private final Object[][] data;

	/**
	 * This is the registry that will be created when the cache is available and when {@link DashRegistryReader#get(int)} will be used.
	 */
	public DashRegistryReader(AbstractDataChunk<?, ?>[] data) {
		this.dataChunks = new DataChunk[data.length];
		this.data = new Object[data.length][];
	}

	public void export() {
		for (int i = 0; i < dataChunks.length; i++) {
			var chunk = dataChunks[i];
			var dataObjects = new Object[chunk.getSize()];
			data[i] = dataObjects;
			chunk.export(dataObjects, this);
		}
	}

	@SuppressWarnings("unchecked")
	public final <R> R get(int pointer) {
		return (R) data[pointer & 0x3f][pointer >>> 6];
	}
}
