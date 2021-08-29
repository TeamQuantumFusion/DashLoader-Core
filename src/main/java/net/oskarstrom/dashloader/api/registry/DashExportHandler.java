package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Applyable;
import net.oskarstrom.dashloader.api.registry.export.SoloExportDataImpl;

public interface DashExportHandler extends Applyable {

	<F> F get(int pointer);

	void addStorage(SoloExportDataImpl<?, ?> registryStorageData, int pos);
}
