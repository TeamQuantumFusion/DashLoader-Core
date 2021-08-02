package net.oskarstrom.dashloader.api.registry;

public interface RegistryStorage<F> {
	int add(F object);

	F get(int pointer);
}
