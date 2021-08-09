package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Applyable;

public interface DashRegistry extends Applyable {
	<F> Pointer add(F object);

	<F> F get(Pointer pointer);
}
