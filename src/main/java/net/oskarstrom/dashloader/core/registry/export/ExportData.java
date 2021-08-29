package net.oskarstrom.dashloader.core.registry.export;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;

public interface ExportData<F, D extends Dashable<F>> {

	F[] allocateArray();

	void export(F[] array, DashExportHandler exportHandler);

	byte getPos();
}
