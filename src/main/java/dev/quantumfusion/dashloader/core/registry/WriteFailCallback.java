package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.Dashable;

@FunctionalInterface
public interface WriteFailCallback<R, D extends Dashable<R>> {
	D fail(R object, DashRegistryWriter writer);
}
