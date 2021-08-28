package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashExportHandler;

public interface Applyable {
	void apply(DashExportHandler registry);
}
