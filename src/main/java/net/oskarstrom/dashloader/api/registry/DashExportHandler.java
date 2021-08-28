package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Applyable;
import net.oskarstrom.dashloader.core.registry.ExportDataImpl;

public interface DashExportHandler extends Applyable {

	<F> F get(int pointer);

	void addStorage(ExportDataImpl<?, ?> registryStorageData, int pos);
}
