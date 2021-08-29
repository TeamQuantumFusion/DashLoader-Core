package net.oskarstrom.dashloader.core.registry;

import net.oskarstrom.dashloader.api.registry.DashExportHandler;
import net.oskarstrom.dashloader.api.registry.Pointer;
import net.oskarstrom.dashloader.api.registry.export.SoloExportDataImpl;

public class DashExportHandlerImpl implements DashExportHandler {
	public final SoloExportDataImpl<?, ?>[] data;

	public DashExportHandlerImpl(int size) {
		this.data = new SoloExportDataImpl[size];
	}

	public void addStorage(SoloExportDataImpl<?, ?> registryStorageData, int pos) {
		data[pos] = registryStorageData;
	}

	public <F> F get(int pointer) {
		final SoloExportDataImpl<?, ?> registryStorage = data[(byte) (pointer & 0x3f)];
		if (registryStorage == null) {
			throw new IllegalStateException("Registry storage " + Pointer.getRegistryPointer(pointer) + " does not exist.");
		}
		//noinspection unchecked
		return (F) registryStorage.dashables[pointer >>> 6];
	}

	@Override
	public void apply(DashExportHandler registry) {
		for (var storage : data)
			storage.export(registry);
	}
}
