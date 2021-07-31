package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.List;

public class Pointer2PointerMap extends DashMap<Pointer2PointerMap.Entry> {
	public Pointer2PointerMap(List<Entry> data) {
		super(data);
	}

	public Pointer2PointerMap() {
	}

	public Pointer2PointerMap(int size) {
		super(size);
	}

	public static class Entry {
		@Serialize(order = 0)
		public final int key;
		@Serialize(order = 1)
		public final int value;

		public Entry(@Deserialize("key") int key,
					 @Deserialize("value") int value) {
			this.key = key;
			this.value = value;
		}

		public static Entry of(int key, int value) {
			return new Entry(key, value);
		}

	}
}
