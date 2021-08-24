package net.oskarstrom.dashloader.api.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;

import java.util.Objects;

// TODO: waiting for ActiveJ to add support
@SuppressWarnings("ClassCanBeRecord")
public class Pointer {
	@Serialize(order = 0)
	public final int objectPointer;
	@Serialize(order = 1)
	public final byte registryPointer;

	public Pointer(@Deserialize("objectPointer") int objectPointer, @Deserialize("registryPointer") byte registryPointer) {
		this.objectPointer = objectPointer;
		this.registryPointer = registryPointer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pointer pointer = (Pointer) o;
		return objectPointer == pointer.objectPointer && registryPointer == pointer.registryPointer;
	}

	@Override
	public int hashCode() {
		return Objects.hash(objectPointer, registryPointer);
	}
}
