package net.oskarstrom.dashloader.core;

import net.oskarstrom.dashloader.core.registry.DashExportHandler;

public interface Dashable<F> {
	F toUndash(DashExportHandler exportHandler);
}
