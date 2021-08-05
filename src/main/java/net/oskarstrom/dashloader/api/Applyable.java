package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashRegistry;

public interface Applyable {
	void apply(DashRegistry registry);
}
