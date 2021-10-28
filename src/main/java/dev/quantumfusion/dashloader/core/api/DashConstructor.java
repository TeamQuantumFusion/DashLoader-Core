package dev.quantumfusion.dashloader.core.api;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;

public class DashConstructor<R, D extends Dashable<R>> {


	public D invoke(R raw, DashRegistryWriter registry) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <F, D extends Dashable<F>> DashConstructor<F, D> create(Class<?> dashClass, Class<?> rawClass) {
		return null;
	}

}
