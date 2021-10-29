package dev.quantumfusion.dashloader.core.common;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public record ObjectIntList<K>(List<ObjectIntEntry<K>> list) {
	public ObjectIntList() {
		this(new ArrayList<>());
	}

	public void put(K key, int value) {
		list.add(new ObjectIntEntry<>(key, value));
	}

	public void forEach(ObjectIntConsumer<K> c) {
		list.forEach(v -> c.accept(v.key, v.value));
	}

	@FunctionalInterface
	public interface ObjectIntConsumer<K> {
		void accept(K key, int value);
	}

	@Data
	public record ObjectIntEntry<K>(K key, int value) {
	}
}