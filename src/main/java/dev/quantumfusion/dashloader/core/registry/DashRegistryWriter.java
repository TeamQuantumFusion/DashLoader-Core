package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractChunkWriter;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class DashRegistryWriter {
	private final Object2IntOpenHashMap<?> dedup = new Object2IntOpenHashMap<>();
	private final Object2ByteMap<Class<?>> target2chunkMappings;
	private final Object2ByteMap<Class<?>> dash2chunkMappings;
	private final Object2ByteMap<Class<?>> mappings;
	private final AbstractChunkWriter<?, ?>[] chunks;

	/**
	 * This is the registry that will be created when the cache is not available and when {@link DashRegistryWriter#add(Object)} will be used.
	 */
	public DashRegistryWriter(AbstractChunkWriter<?, ?>[] chunks) {
		this.target2chunkMappings = new Object2ByteOpenHashMap<>();
		this.dash2chunkMappings = new Object2ByteOpenHashMap<>();
		this.mappings = new Object2ByteOpenHashMap<>();
		this.chunks = chunks;
	}

	void compileMappings() {
		for (int i = 0; i < chunks.length; i++) {
			for (Class<?> aClass : chunks[i].getClasses())
				mappings.put(aClass, (byte) i);
		}
	}

	void addChunkMapping(Class<?> chunkDashType, byte pos) {
		dash2chunkMappings.put(chunkDashType, pos);
		final DashObject declaredAnnotation = chunkDashType.getDeclaredAnnotation(DashObject.class);
		if (declaredAnnotation != null) target2chunkMappings.put(declaredAnnotation.value(), pos);
		else throw new RuntimeException("No DashObject annotation for " + chunkDashType.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public <R, D extends Dashable<R>> AbstractChunkWriter<R, D> getChunk(Class<D> dashType) {
		return (AbstractChunkWriter<R, D>) chunks[dash2chunkMappings.getByte(dashType)];
	}

	public <R, D extends Dashable<R>> int addDirect(AbstractChunkWriter<R, D> chunk, R object) {
		final int objectPos = chunk.add(object);
		final int pointer = createPointer(objectPos, chunk.pos);
		((Object2IntMap<R>) dedup).put(object, pointer);
		return pointer;
	}

	@SuppressWarnings("unchecked")
	public <R> int add(R object) {
		if (dedup.containsKey(object)) return dedup.getInt(object);
		var targetClass = object.getClass();
		byte chunkPos = mappings.getOrDefault(targetClass, (byte) -1);

		if (chunkPos == -1) {
			for (var targetChunk : target2chunkMappings.object2ByteEntrySet()) {
				if (targetChunk.getKey().isAssignableFrom(targetClass)) {
					chunkPos = targetChunk.getByteValue();
					break;
				}
			}
		}

		if (chunkPos == -1)
			throw new RuntimeException("Could not find a ChunkWriter for " + targetClass);

		var chunk = (AbstractChunkWriter<R, ?>) chunks[chunkPos];
		final var objectPos = chunk.add(object);
		final int pointer = createPointer(objectPos, chunkPos);
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
