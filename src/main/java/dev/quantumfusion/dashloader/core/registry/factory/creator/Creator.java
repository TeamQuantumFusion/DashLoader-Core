package dev.quantumfusion.dashloader.core.registry.factory.creator;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;

public interface Creator<R, D extends Dashable<R>> {
	D create(R raw, RegistryWriter writer) throws Throwable;
}
