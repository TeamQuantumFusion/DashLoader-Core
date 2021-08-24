package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.registry.Pointer;

import java.util.List;
import java.util.Objects;

public class Pointer2ObjectMap<O> extends DashMap<Pointer2ObjectMap.Entry<O>> {
	public Pointer2ObjectMap(List<Entry<O>> data) {
		super(data);
	}

	public Pointer2ObjectMap() {
	}

	public Pointer2ObjectMap(int size) {
		super(size);
	}


	public static class Entry<O> implements DashEntry<Pointer, O> {
		@Serialize(order = 0)
		public final Pointer key;
		@Serialize(order = 1)
		public final O value;

		public Entry(@Deserialize("key") Pointer key,
					 @Deserialize("value") O value) {
			this.key = key;
			this.value = value;
		}

		public static <O> Entry<O> of(Pointer key, O value) {
			return new Entry<>(key, value);
		}

		@Override
		public Pointer getKey() {
			return key;
		}

		@Override
		public O getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Entry<?> entry = (Entry<?>) o;
			return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}
	}
}
