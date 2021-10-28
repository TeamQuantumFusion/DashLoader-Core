package dev.quantumfusion.dashloader.core.registry.chunk;

import dev.quantumfusion.hyphen.scan.annotations.Data;

public abstract class AbstractChunk {
	@Data
	public final byte pos;

	public AbstractChunk(byte pos) {
		this.pos = pos;
	}
}
