package net.oskarstrom.dashloader.core.registry.export;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeSubclasses;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.ThreadManager;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;

public class MultiStageExportData<F, D extends Dashable<F>> implements ExportData<F, D> {
	@Serialize(order = 0)
	@SerializeSubclasses(path = 0)
	// first is stage, then the object
	public final ThreadManager.PosEntry<D>[][] dashables;
	@Serialize(order = 1)
	public final byte registryPos;
	@Serialize(order = 3)
	public final int dashablesSize;


	public MultiStageExportData(@Deserialize("dashables") ThreadManager.PosEntry<D>[][] dashables,
								@Deserialize("registryPos") byte registryPos,
								@Deserialize("dashablesSize") int dashablesSize) {
		this.dashables = dashables;
		this.registryPos = registryPos;
		this.dashablesSize = dashablesSize;
	}


	@Override
	public F[] allocateArray() {
		return (F[]) new Object[dashablesSize];
	}

	@Override
	public void export(F[] array, DashExportHandler exportHandler) {
		if (dashables == null || dashables.length == 0) {
			throw new IllegalStateException("Dashables are not available.");
		}
		for (ThreadManager.PosEntry<D>[] objects : dashables) {
			ThreadManager.parallelToUndash(exportHandler, objects, array);
		}
	}

	@Override
	public byte getPos() {
		return registryPos;
	}


}
