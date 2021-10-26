package net.oskarstrom.dashloader.core.system.dashobjects;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.system.objects.Sprite;

@Data
@DashObject(Sprite.class)
public record DashSprite(int veryNice) implements Dashable<Sprite> {
	public static DashSprite create(Sprite sprite) {
		return new DashSprite(sprite.veryNice);
	}

	@Override
	public Sprite toUndash(DashExportHandler exportHandler) {
		return new Sprite(veryNice);
	}
}
