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

public class FloatingWriteChunk<R, D extends Dashable<R>> extends AbstractWriteChunk<R, D> {
	private final List<R> list = new ArrayList<>();

	public FloatingWriteChunk(byte pos, String name, RegistryWriter writer, Collection<DashObjectClass<R, D>> dashObjects, DashFactory<R, D> factory) {
		super(pos, name, writer, factory, dashObjects);
	}

	@Override
	public int add(R raw) {
		final int pos = list.size();
		list.add(raw);
		return pos;
	}

	@Override
	public AbstractDataChunk<R, D> exportData() {
		final int length = list.size();
		final D[] dashables = (D[]) new Dashable[length];
		for (int i = 0; i < length; i++) {
			dashables[i] = factory.create(list.get(i), writer);
		}
		return new DataChunk<>(pos, name, dashables);
	}
}
