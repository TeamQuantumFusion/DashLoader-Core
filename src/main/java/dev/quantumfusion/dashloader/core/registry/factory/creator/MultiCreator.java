package dev.quantumfusion.dashloader.core.registry.factory.creator;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collection;

public class MultiCreator<R, D extends Dashable<R>> implements Creator<R, D> {
	private final Object2ObjectMap<Class<R>, SoloCreator<R, D>> creatorMap;

	private MultiCreator(Object2ObjectMap<Class<R>, SoloCreator<R, D>> creatorMap) {
		this.creatorMap = creatorMap;
	}

	public static <R, D extends Dashable<R>> MultiCreator<R, D> create(Collection<DashObjectClass<R, D>> dashObjects) {
		var creatorMap = new Object2ObjectOpenHashMap<Class<R>, SoloCreator<R, D>>();
		for (DashObjectClass<R, D> dashObject : dashObjects) {
			creatorMap.put(dashObject.getTargetClass(), SoloCreator.create(dashObject));
		}
		return new MultiCreator<>(creatorMap);
	}

	@Override
	public D create(R raw, RegistryWriter writer) throws Throwable {
		var creator = creatorMap.get(raw.getClass());
		if (creator != null) {
			return creator.create(raw, writer);
		} else throw new CreationError();
	}
}
