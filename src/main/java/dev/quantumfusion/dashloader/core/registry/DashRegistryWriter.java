package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.registry.chunk.write.ChunkWriter;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

public class DashRegistryWriter {
	private final Object2ByteMap<Class<?>> mappings;
	private final ChunkWriter<?, ?>[] chunks;

	/**
	 * This is the registry that will be created when the cache is not available and when {@link DashRegistryWriter#add(Object)} will be used.
	 */
	public DashRegistryWriter(ChunkWriter<?, ?>[] chunks) {
		this.mappings = new Object2ByteOpenHashMap<>();
		this.chunks = chunks;
	}

	public void createMappings() {
		for (int i = 0; i < chunks.length; i++) {
			for (Class<?> aClass : chunks[i].getClasses()) mappings.put(aClass, (byte) i);
		}
	}

	public static int createPointer(int objectPos, byte chunkPos) {
		if (chunkPos > 0b111111)
			throw new IllegalStateException("Chunk pos is too big. " + chunkPos + " > " + 0x3f);
		if (objectPos > 0x3ffffff)
			throw new IllegalStateException("Object pos is too big. " + objectPos + " > " + 0x3ffffff);
		return objectPos << 6 | (chunkPos & 0x3f);
	}

	@SuppressWarnings("unchecked")
	public <R> int add(R object) {
		final var targetClass = object.getClass();
		final var chunk = (ChunkWriter<R, ?>) chunks[mappings.getByte(targetClass)];
		final var objectPos = chunk.add(object);
		return createPointer(objectPos, chunk.pos);
	}
}
