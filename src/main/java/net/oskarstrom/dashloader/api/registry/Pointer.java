package net.oskarstrom.dashloader.api.registry;

// TODO: waiting for ActiveJ to add support
@SuppressWarnings("ClassCanBeRecord")
public class Pointer {
	public final int objectPointer;
	public final byte registryPointer;

	public Pointer(int objectPointer, byte registryPointer) {
		this.objectPointer = objectPointer;
		this.registryPointer = registryPointer;
	}
}
