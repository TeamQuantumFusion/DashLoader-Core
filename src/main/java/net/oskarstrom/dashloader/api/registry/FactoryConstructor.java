package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

public interface FactoryConstructor<F, D extends Dashable<F>> {
	D create(F object, DashRegistry registry);
}
