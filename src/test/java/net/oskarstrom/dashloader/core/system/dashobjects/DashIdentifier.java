package net.oskarstrom.dashloader.core.system.dashobjects;

import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.annotations.DashObject;
import net.oskarstrom.dashloader.api.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.system.objects.Identifier;

@DashObject(Identifier.class)
public class DashIdentifier implements Dashable<Identifier> {
	@Serialize
	public String string;

	public DashIdentifier(String string) {
		this.string = string;
	}

	public DashIdentifier(Identifier identifier) {
		this.string = identifier.string;
	}

	@Override
	public Identifier toUndash(DashExportHandler exportHandler) {
		return new Identifier(string);
	}
}
