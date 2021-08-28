package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

public interface RegistryStorage<F> {
	int add(F object);

	Dashable<F>[] getDashables();
}
