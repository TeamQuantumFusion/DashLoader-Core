package dev.quantumfusion.dashloader.core.registry.chunk.data;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
public class DataChunk<R, D extends Dashable<R>> extends AbstractDataChunk<R, D> {
	public final D[] dashables;

	public DataChunk(byte pos, D[] dashables) {
		super(pos);
		this.dashables = dashables;
	}

	@Override
	public void export(Object[] data, DashRegistryReader registry) {
		DashThreading.export(dashables, data, registry);
	}

	@Override
	public int getSize() {
		return dashables.length;
	}

}
