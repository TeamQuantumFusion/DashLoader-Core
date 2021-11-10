package dev.quantumfusion.dashloader.core.registry.chunk;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
public abstract class AbstractChunk<R, D extends Dashable<R>> {
	public final byte pos;
	public final String name;

	protected AbstractChunk(byte pos, String name) {
		this.pos = pos;
		this.name = name;
	}
}
