package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.registry.Pointer;

import java.util.List;
import java.util.Objects;

public class Pointer2PointerMap extends DashMap<Pointer2PointerMap.Entry> {
	public Pointer2PointerMap(List<Entry> data) {
		super(data);
	}

	public Pointer2PointerMap() {
		super();
	}

	public Pointer2PointerMap(int size) {
		super(size);
	}

	public static class Entry implements DashEntry<Pointer, Pointer> {
		@Serialize(order = 0)
		public final Pointer key;
		@Serialize(order = 1)
		public final Pointer value;

		public Entry(@Deserialize("key") Pointer key,
					 @Deserialize("value") Pointer value) {
			this.key = key;
			this.value = value;
		}

		public static Entry of(Pointer key, Pointer value) {
			return new Entry(key, value);
		}

		@Override
		public Pointer getKey() {
			return key;
		}

		@Override
		public Pointer getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Entry entry = (Entry) o;
			return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}
	}
}
