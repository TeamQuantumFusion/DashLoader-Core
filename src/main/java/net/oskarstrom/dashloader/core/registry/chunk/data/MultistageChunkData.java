package net.oskarstrom.dashloader.core.registry.chunk.data;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.chunk.AbstractRegistryChunk;

public class MultistageChunkData<R, D extends Dashable<R>> implements RegistryChunkData<R, D> {

	@Override
	public AbstractRegistryChunk<R, D> export() {
		return null;
	}
}
