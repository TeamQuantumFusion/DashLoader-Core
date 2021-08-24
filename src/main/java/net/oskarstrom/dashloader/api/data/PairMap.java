package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.List;
import java.util.Objects;

public class PairMap<K, V> extends DashMap<PairMap.Entry<K, V>> {

	public PairMap(List<Entry<K, V>> data) {
		super(data);
	}

	public PairMap() {
	}

	public PairMap(int size) {
		super(size);
	}

	public static class Entry<K, V> implements DashEntry<K, V> {
		@Serialize(order = 0)
		public final K key;
		@Serialize(order = 1)
		public final V value;

		public Entry(@Deserialize("key") K key,
					 @Deserialize("value") V value) {
			this.key = key;
			this.value = value;
		}

		public static <K, V> Entry<K, V> of(K key, V value) {
			return new Entry<>(key, value);
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Entry<?, ?> entry = (Entry<?, ?>) o;
			return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}
	}
}
