package net.oskarstrom.dashloader.core.registry.storage;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.regdata.RegistryData;

public interface RegistryStorage<F> {
	int add(F object);

	<D extends Dashable<F>> RegistryData<F, D> getExportData();
}
