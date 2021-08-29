package net.oskarstrom.dashloader.api.registry.export;

import net.oskarstrom.dashloader.api.registry.DashExportHandler;

public interface ExportData {
	void export(DashExportHandler exportHandler);
}
