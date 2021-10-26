package net.oskarstrom.dashloader.core.data;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.List;

@Data
public record IntIntList(List<IntInt> list) {

	public void put(int key, int value) {
		list.add(new IntInt(key, value));
	}

	public void forEach(IntIntConsumer c) {
		list.forEach(v -> c.accept(v.key, v.value));
	}

	@FunctionalInterface
	public interface IntIntConsumer {
		void accept(int key, int value);
	}

	@Data
	public record IntInt(int key, int value) {
	}
}
