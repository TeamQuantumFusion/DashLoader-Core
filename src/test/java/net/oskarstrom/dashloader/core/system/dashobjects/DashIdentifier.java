package net.oskarstrom.dashloader.core.system.dashobjects;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.system.objects.Identifier;


@Data
@DashObject(Identifier.class)
public record DashIdentifier(String string) implements Dashable<Identifier> {
	public static DashIdentifier create(Identifier identifier) {
		return new DashIdentifier(identifier.string);
	}

	@Override
	public Identifier toUndash(DashExportHandler exportHandler) {
		return new Identifier(string);
	}
}
