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

	}
}
