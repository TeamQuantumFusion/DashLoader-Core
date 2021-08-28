package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.DashExportHandler;

public interface Dashable<F> {
	F toUndash(DashExportHandler exportHandler);
}
