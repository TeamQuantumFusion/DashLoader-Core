package net.oskarstrom.dashloader.api.registry.storage;

import net.oskarstrom.dashloader.api.registry.export.ExportData;

public interface RegistryStorage<F> {
	int add(F object);

	ExportData getExportData(byte pos);
}
