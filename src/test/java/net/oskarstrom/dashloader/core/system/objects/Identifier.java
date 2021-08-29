package net.oskarstrom.dashloader.core.system.objects;

public class Identifier {
	public String string;

	public Identifier(String string) {
		this.string = string;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Identifier that = (Identifier) o;

		return string != null ? string.equals(that.string) : that.string == null;
	}

	@Override
	public int hashCode() {
		return string != null ? string.hashCode() : 0;
	}
}
