package net.oskarstrom.dashloader.core.system.objects;

public class Sprite {
	public final int veryNice;

	public Sprite(int veryNice) {
		this.veryNice = veryNice;
	}


	@Override
	public boolean equals(Object o) {


		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Sprite sprite = (Sprite) o;

		return veryNice == sprite.veryNice;
	}

	@Override
	public int hashCode() {
		return veryNice;
	}
}
