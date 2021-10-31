package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;

@SuppressWarnings("FinalMethodInFinalClass")
public final class DashRegistryReader {
	private final AbstractDataChunk<?, ?>[] dataChunks;

	// Holds an array of the exported datachunks array values.
	private final Object[][] data;

	/**
	 * This is the registry that will be created when the cache is available and when {@link DashRegistryReader#get(int)} will be used.
	 */
	public DashRegistryReader(AbstractDataChunk<?, ?>[] data) {
		this.dataChunks = data;
		this.data = new Object[data.length][];
	}

	public void export() {
		for (int i = 0; i < dataChunks.length; i++) {
			var chunk = dataChunks[i];
			final int size = chunk.getSize();
			var dataObjects = new Object[size];
			data[i] = dataObjects;
			System.out.println("Loading " + size + " " + chunk.name + "s");
			chunk.prepare(this);
			chunk.export(dataObjects, this);
			chunk.apply(this);
		}
	}

	@SuppressWarnings("unchecked")
	public final <R> R get(final int pointer) {
		return (R) data[pointer & 0x3f][pointer >>> 6];
	}
}
