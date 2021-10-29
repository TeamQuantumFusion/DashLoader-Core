package dev.quantumfusion.dashloader.core.common;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public record IntObjectList<V>(List<IntObjectEntry<V>> list) {
	public IntObjectList() {
		this(new ArrayList<>());
	}

	public void put(int key, V value) {
		list.add(new IntObjectEntry<>(key, value));
	}

	public void forEach(IntObjectConsumer<V> c) {
		list.forEach(v -> c.accept(v.key, v.value));
	}

	@FunctionalInterface
	public interface IntObjectConsumer<V> {
		void accept(int key, V value);
	}

	@Data
	public record IntObjectEntry<V>(int key, V value) {
	}
}
