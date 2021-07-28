package net.oskarstrom.dashloader.api.registry;

public interface DashRegistry {
	<F> Pointer add(F object);

	<F> F get(Pointer pointer);


}

