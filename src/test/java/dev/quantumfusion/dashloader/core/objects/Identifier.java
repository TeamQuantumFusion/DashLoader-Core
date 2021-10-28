package dev.quantumfusion.dashloader.core.objects;

import java.util.Objects;

public class Identifier {
	public String text;

	public Identifier(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;
		return Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text);
	}
}
