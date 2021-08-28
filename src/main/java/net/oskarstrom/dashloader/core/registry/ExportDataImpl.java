package net.oskarstrom.dashloader.core.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.ThreadManager;
import net.oskarstrom.dashloader.api.registry.DashExportHandler;
import net.oskarstrom.dashloader.api.registry.ExportData;

public class ExportDataImpl<F, D extends Dashable<F>> implements ExportData {
	@Serialize(order = 0)
	public final D[] dashables;
	@Serialize(order = 1)
	public final byte registryPos;
	@Serialize(order = 2)
	public final short priority;

	public F[] exportedObjects;


	public ExportDataImpl(@Deserialize("dashables") D[] dashables,
						  @Deserialize("registryPos") byte registryPos,
						  @Deserialize("priority") short priority) {
		this.dashables = dashables;
		this.registryPos = registryPos;
		this.priority = priority;
	}


	@Override
	public void export(DashExportHandler exportHandler) {
		if (dashables == null || dashables.length == 0) {
			throw new IllegalStateException("Dashables are not available.");
		}
		exportedObjects = (F[]) new Object[dashables.length];
		ThreadManager.parallelToUndash(exportHandler, dashables, exportedObjects);
	}
}
