package net.oskarstrom.dashloader.core.registry.chunk;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.chunk.data.RegistryChunkData;

public class SoloChunk<R, D extends Dashable<R>> implements RegistryChunk<R, D> {
	@Override
	public int add(R object) {
		return 0;
	}

	@Override
	public R get(int id) {
		return null;
	}

	@Override
	public RegistryChunkData<R, D> exportData() {
		return null;
	}

	@Override
	public void importData(RegistryChunkData<R, D> data) {

	}
}
