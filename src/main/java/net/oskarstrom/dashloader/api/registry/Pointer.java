package net.oskarstrom.dashloader.api.registry;


public class Pointer {

	public static int parsePointer(int objectPointer, byte registryPointer) {
		if (registryPointer > 0x3f)
			throw new IllegalStateException("Registry pointer is too big. " + registryPointer + " > " + 63);
		if (objectPointer > 0x3ffffff) {
			throw new IllegalStateException("Object pointer is too big. " + objectPointer + " > " + 67108863);
		}
		return objectPointer << 6 | (registryPointer & 0x3f);
	}

	public static int getObjectPointer(int pointer) {
		return pointer >>> 6;
	}

	public static byte getRegistryPointer(int pointer) {
		return (byte) (pointer & 0x3f);
	}

}
