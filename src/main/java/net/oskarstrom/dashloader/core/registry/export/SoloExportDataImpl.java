package net.oskarstrom.dashloader.core.registry.export;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.ThreadManager;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;

public class SoloExportDataImpl<F, D extends Dashable<F>> implements ExportData<F, D> {
	@Serialize(order = 0)
	public final D[] dashables;
	@Serialize(order = 1)
	public final byte registryPos;


	public SoloExportDataImpl(@Deserialize("dashables") D[] dashables,
							  @Deserialize("registryPos") byte registryPos) {
		this.dashables = dashables;
		this.registryPos = registryPos;
	}


	@Override
	public F[] allocateArray() {
		return (F[]) new Object[dashables.length];
	}

	@Override
	public void export(F[] array, DashExportHandler exportHandler) {
		if (dashables == null || dashables.length == 0) {
			throw new IllegalStateException("Dashables are not available.");
		}
		ThreadManager.parallelToUndash(exportHandler, dashables, array);
	}

	@Override
	public byte getPos() {
		return registryPos;
	}
}
