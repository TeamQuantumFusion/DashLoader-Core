package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.List;

public class Pointer2ObjectMap<O> extends DashMap<Pointer2ObjectMap.Entry<O>> {
	public Pointer2ObjectMap(List<Entry<O>> data) {
		super(data);
	}

	public Pointer2ObjectMap() {
	}

	public Pointer2ObjectMap(int size) {
		super(size);
	}

	public static class Entry<O> {
		@Serialize(order = 0)
		public final int key;
		@Serialize(order = 1)
		public final O value;

		public Entry(@Deserialize("key") int key,
					 @Deserialize("value") O value) {
			this.key = key;
			this.value = value;
		}

		public static <O> Entry<O> of(int key, O value) {
			return new Entry<>(key, value);
		}

	}
}
