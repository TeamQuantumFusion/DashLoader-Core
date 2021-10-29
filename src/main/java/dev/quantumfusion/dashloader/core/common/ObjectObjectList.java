package dev.quantumfusion.dashloader.core.common;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Data
public record ObjectObjectList<K, V>(List<ObjectObjectEntry<K, V>> list) {
	public ObjectObjectList() {
		this(new ArrayList<>());
	}

	public void put(K key, V value) {
		list.add(new ObjectObjectEntry<>(key, value));
	}

	public void forEach(BiConsumer<K, V> c) {
		list.forEach(v -> c.accept(v.key, v.value));
	}

	@Data
	public record ObjectObjectEntry<K, V>(K key, V value) {
	}
}