package net.oskarstrom.dashloader.core.registry.storage;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.registry.FactoryConstructor;
import net.oskarstrom.dashloader.core.registry.export.SoloExportDataImpl;

public class SoloRegistryStorage<F, D extends Dashable<F>> extends RegistryStorageImpl<F, D> {
	private final FactoryConstructor<F, D> constructor;

	public SoloRegistryStorage(FactoryConstructor<F, D> constructor, DashRegistry registry, Class<?> tag) {
		super(registry, tag);
		this.constructor = constructor;
	}

	@Override
	public D create(F object, DashRegistry registry) {
		return constructor.create(object, registry);
	}

	@Override
	public SoloExportDataImpl<F, D> getExportData() {
		final byte storageId = registry.getStorageId(tag);
		//noinspection unchecked
		return new SoloExportDataImpl<>(dashables.toArray(new Dashable[0]), storageId);
	}
}
