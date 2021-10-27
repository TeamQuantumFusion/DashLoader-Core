package net.oskarstrom.dashloader.core.registry;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.regdata.RegistryData;

public class DashExportHandler {
	public final Object[][] data;
	public final RegistryData<?, ?>[] exportdata;

	public DashExportHandler(int size) {
		this.data = new Object[size][];
		this.exportdata = new RegistryData[size];
	}

	public void addStorage(RegistryData<?, ?> registryStorageData) {
		exportdata[registryStorageData.getPos()] = registryStorageData;
	}

	public <F> F get(int pointer) {
		final Object[] registryStorage = data[(byte) (pointer & 0x3f)];
		if (registryStorage == null) {
			throw new IllegalStateException("Registry storage " + Pointer.getRegistryPointer(pointer) + " does not exist.");
		}
		//noinspection unchecked
		return (F) registryStorage[pointer >>> 6];
	}

	public void export() {
		for (int i = 0, exportdataLength = exportdata.length; i < exportdataLength; i++) {
			exportData(exportdata[i], i);
		}
	}

	private <F, D extends Dashable<F>> void exportData(RegistryData<?, ?> data, int pos) {
		//noinspection unchecked
		RegistryData<F, D> castedData = (RegistryData<F, D>) data;
		final F[] objects = castedData.allocateArray();
		this.data[pos] = objects;
		castedData.export(objects, this);
	}
}
