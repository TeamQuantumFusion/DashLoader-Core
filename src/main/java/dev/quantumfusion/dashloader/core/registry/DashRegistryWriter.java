package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.chunk.write.ChunkWriter;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class DashRegistryWriter {
	private final Object2IntMap<?> dedup = new Object2IntOpenHashMap<>();
	private final Object2ByteMap<Class<?>> dashTypeMappings;
	private final Object2ByteMap<Class<?>> mappings;
	private final ChunkWriter<?, ?>[] chunks;

	/**
	 * This is the registry that will be created when the cache is not available and when {@link DashRegistryWriter#add(Object)} will be used.
	 */
	public DashRegistryWriter(ChunkWriter<?, ?>[] chunks) {
		this.dashTypeMappings = new Object2ByteOpenHashMap<>();
		this.mappings = new Object2ByteOpenHashMap<>();
		this.chunks = chunks;
	}

	void compileMappings() {
		for (int i = 0; i < chunks.length; i++) {
			for (Class<?> aClass : chunks[i].getClasses())
				mappings.put(aClass, (byte) i);
		}
	}

	void addDashTypeMapping(Class<?> dashType, byte pos) {
		dashTypeMappings.put(dashType, pos);
	}

	@SuppressWarnings("unchecked")
	public <R, D extends Dashable<R>> ChunkWriter<R, D> getChunk(Class<D> dashType) {
		return (ChunkWriter<R, D>) chunks[dashTypeMappings.getByte(dashType)];
	}

	@SuppressWarnings("unchecked")
	public <R> int add(R object) {
		if (dedup.containsKey(object)) return dedup.getInt(object);
		final var targetClass = object.getClass();
		final var chunk = (ChunkWriter<R, ?>) chunks[mappings.getByte(targetClass)];
		final var objectPos = chunk.add(object);
		final int pointer = createPointer(objectPos, chunk.pos);
		((Object2IntMap<R>) dedup).put(object, pointer);
		return pointer;
	}

	public static int createPointer(int objectPos, byte chunkPos) {
		if (chunkPos > 0b111111)
			throw new IllegalStateException("Chunk pos is too big. " + chunkPos + " > " + 0x3f);
		if (objectPos > 0x3ffffff)
			throw new IllegalStateException("Object pos is too big. " + objectPos + " > " + 0x3ffffff);
		return objectPos << 6 | (chunkPos & 0x3f);
	}
}
