package net.oskarstrom.dashloader.api.registry;

// TODO: waiting for ActiveJ to add support
@SuppressWarnings("ClassCanBeRecord")
public class Pointer {
	public static int parsePointer(int objectPointer, byte registryPointer) {
		if (registryPointer < 0) {
			throw new IllegalStateException("Registry pointer is overflowing");
		}
		return (objectPointer << 4) | ((int) registryPointer);
	}

	public static int getObjectPointer(int pointer) {
		return pointer >>> 4;
	}

	public static int getRegistryPointer(int pointer) {
		return (byte) pointer;
	}


}
