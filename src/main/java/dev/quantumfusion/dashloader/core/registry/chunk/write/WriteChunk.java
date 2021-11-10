package dev.quantumfusion.dashloader.core.registry.chunk.write;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.data.DataChunk;
import dev.quantumfusion.dashloader.core.registry.factory.DashFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WriteChunk<R, D extends Dashable<R>> extends AbstractWriteChunk<R, D> {
	private final List<D> list = new ArrayList<>();

	public WriteChunk(byte pos, String name, RegistryWriter writer, Collection<DashObjectClass<R, D>> dashObjects, DashFactory<R, D> factory) {
		super(pos, name, writer, factory, dashObjects);
	}

	@Override
	public int add(R raw) {
		final int pos = list.size();
		list.add(factory.create(raw, writer));
		return pos;
	}

	@Override
	public AbstractDataChunk<R, D> exportData() {
		final D[] dashables = (D[]) list.toArray(Dashable[]::new);
		return new DataChunk<>(pos, name, dashables);
	}
}
