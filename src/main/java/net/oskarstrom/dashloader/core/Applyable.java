package net.oskarstrom.dashloader.core;

import net.oskarstrom.dashloader.core.registry.DashExportHandler;

public interface Applyable {
	void apply(DashExportHandler registry);
}
