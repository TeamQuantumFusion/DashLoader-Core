package net.oskarstrom.dashloader.core.data;

import java.util.Map;

public interface DashEntry<K, V> extends Map.Entry<K, V> {

	default V setValue(V value) {
		throw new UnsupportedOperationException();
	}
}
