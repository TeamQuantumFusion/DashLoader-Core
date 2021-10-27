package net.oskarstrom.dashloader.core.registry.regdata;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;

public interface RegistryData<F, D extends Dashable<F>> {

	F[] allocateArray();

	void export(F[] array, DashExportHandler exportHandler);

	byte getPos();
}
