package dev.quantumfusion.dashloader.core.registry;


import dev.quantumfusion.dashloader.core.DashLoaderCore;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;

@SuppressWarnings("FinalMethodInFinalClass")
public final class RegistryReader {
	private final AbstractDataChunk<?, ?>[] dataChunks;

	// Holds an array of the exported dataChunks array values.
	private final Object[][] data;

	public RegistryReader(AbstractDataChunk<?, ?>[] data) {
		this.dataChunks = data;
		this.data = new Object[data.length][];
	}

	public final void export() {
		for (int i = 0; i < dataChunks.length; i++) {
			var chunk = dataChunks[i];
			final int size = chunk.getDashableSize();
			var dataObjects = new Object[size];
			data[i] = dataObjects;
			DashLoaderCore.CORE.info("Loading " + size + " " + chunk.name + "s");
			chunk.preExport(this);
			chunk.export(dataObjects, this);
			chunk.postExport(this);
		}
	}

	@SuppressWarnings("unchecked")
	public final <R> R get(final int pointer) {
		// inlining go brrr
		return (R) data[pointer & 0x3f][pointer >>> 6];
	}
}
