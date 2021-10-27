package net.oskarstrom.dashloader.core.registry.storage;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.registry.FactoryConstructor;
import net.oskarstrom.dashloader.core.registry.regdata.MultiRegistryDataImpl;
import net.oskarstrom.dashloader.core.registry.regdata.RegistryData;

public class MultiRegistryStorage<F, D extends Dashable<F>> extends RegistryStorageImpl<F, D> {
	private final Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor;


	public MultiRegistryStorage(Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> constructor, DashRegistry registry, Class<?> tag) {
		super(registry, tag);
		this.constructor = constructor;
	}

	@Override
	public D create(F object, DashRegistry registry) {
		final FactoryConstructor<F, D> fdFactoryConstructor = constructor.get(object.getClass());
		if (fdFactoryConstructor == null) {
			//TODO error handling
			throw new IllegalStateException();
		}
		return fdFactoryConstructor.create(object, registry);
	}

	@Override
	public RegistryData<F, D> getExportData() {
		final byte storageId = registry.getStorageId(tag);
		//noinspection unchecked
		return new MultiRegistryDataImpl<>(dashables.toArray(new Dashable[0]), storageId);
	}
}