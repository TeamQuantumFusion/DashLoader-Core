package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashRegistry;

public interface Dashable<F> {

	F toUndash(DashRegistry registry);
}
