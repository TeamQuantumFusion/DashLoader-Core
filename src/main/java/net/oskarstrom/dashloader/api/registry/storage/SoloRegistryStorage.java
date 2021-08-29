package net.oskarstrom.dashloader.api.registry.storage;

import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.FactoryConstructor;
import net.oskarstrom.dashloader.api.registry.export.SoloExportDataImpl;

public class SoloRegistryStorage<F, D extends Dashable<F>> extends RegistryStorageImpl<F, D> {
	public final int priority;
	private final FactoryConstructor<F, D> constructor;

	public SoloRegistryStorage(FactoryConstructor<F, D> constructor, DashRegistry registry, int priority) {
		super(registry);
		this.constructor = constructor;
		this.priority = priority;
	}

	@Override
	public D create(F object, DashRegistry registry) {
		return constructor.create(object, registry);
	}

	@Override
	public SoloExportDataImpl<F, D> getExportData(byte pos) {
		//noinspection unchecked
		return new SoloExportDataImpl<>(dashables.toArray(new Dashable[0]), pos, priority);
	}
}
