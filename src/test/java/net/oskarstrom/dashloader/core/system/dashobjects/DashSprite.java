package net.oskarstrom.dashloader.core.system.dashobjects;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.system.objects.Sprite;

@DashObject(Sprite.class)
public class DashSprite implements Dashable<Sprite> {
	public final int veryNice;

	public DashSprite(int veryNice) {
		this.veryNice = veryNice;
	}

	public DashSprite(Sprite sprite) {
		this.veryNice = sprite.veryNice;
	}

	@Override
	public Sprite toUndash(DashExportHandler exportHandler) {
		return new Sprite(veryNice);
	}
}
