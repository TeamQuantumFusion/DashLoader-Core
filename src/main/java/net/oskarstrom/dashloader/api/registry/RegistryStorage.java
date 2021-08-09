package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

public interface RegistryStorage<F> extends Dashable<F[]> {
	int add(F object);

	F get(int pointer);
}
