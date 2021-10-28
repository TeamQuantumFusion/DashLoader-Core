package net.oskarstrom.dashloader.core.registry.chunk;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.chunk.data.RegistryChunkData;

import java.util.List;

public class MultistageChunk<R, D extends Dashable<R>> extends AbstractRegistryChunk<R, D> {
	public MultistageChunk(byte pos) {
		super(pos);
	}

	@Override
	public int add(R object) {
		return 0;
	}

	@Override
	public R get(int id) {
		return null;
	}

	@Override
	public List<Class<?>> getClasses() {
		return null;
	}

	@Override
	public RegistryChunkData<R, D> exportData() {
		return null;
	}
}
