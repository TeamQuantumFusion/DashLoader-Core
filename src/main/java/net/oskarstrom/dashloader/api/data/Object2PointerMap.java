package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.registry.Pointer;

import java.util.List;

public class Object2PointerMap<O> extends DashMap<Object2PointerMap.Entry<O>> {

	public Object2PointerMap(List<Entry<O>> data) {
		super(data);
	}

	public Object2PointerMap() {
	}

	public Object2PointerMap(int size) {
		super(size);
	}

	public static class Entry<O> {
		@Serialize(order = 0)
		public final O key;
		@Serialize(order = 1)
		public final Pointer value;

		public Entry(@Deserialize("key") O key,
					 @Deserialize("value") Pointer value) {
			this.key = key;
			this.value = value;
		}

		public static <O> Entry<O> of(O key, Pointer value) {
			return new Entry<>(key, value);
		}

	}
}
