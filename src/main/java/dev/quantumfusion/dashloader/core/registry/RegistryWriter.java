package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashObject;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractWriteChunk;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public final class RegistryWriter {
	private final Object2IntOpenHashMap<?> dedup = new Object2IntOpenHashMap<>();
	private final Object2ByteMap<Class<?>> target2chunkMappings;
	private final Object2ByteMap<Class<?>> dashTag2chunkMappings;
	private final Object2ByteMap<Class<?>> mappings;
	private final AbstractWriteChunk<?, ?>[] chunks;

	public RegistryWriter(AbstractWriteChunk<?, ?>[] chunks) {
		this.target2chunkMappings = new Object2ByteOpenHashMap<>();
		this.dashTag2chunkMappings = new Object2ByteOpenHashMap<>();
		this.mappings = new Object2ByteOpenHashMap<>();
		this.chunks = chunks;
	}

	public static int createPointer(int objectPos, byte chunkPos) {
		if (chunkPos > 0b111111)
			throw new IllegalStateException("Chunk pos is too big. " + chunkPos + " > " + 0x3f);
		if (objectPos > 0x3ffffff)
			throw new IllegalStateException("Object pos is too big. " + objectPos + " > " + 0x3ffffff);
		return objectPos << 6 | (chunkPos & 0x3f);
	}

	void compileMappings() {
		for (int i = 0; i < chunks.length; i++) {
			for (Class<?> aClass : chunks[i].getTargetClasses())
				mappings.put(aClass, (byte) i);
		}
	}

	void addChunkMapping(Class<?> tag, byte pos) {
		dashTag2chunkMappings.put(tag, pos);
		final DashObject declaredAnnotation = tag.getDeclaredAnnotation(DashObject.class);
		if (declaredAnnotation != null) target2chunkMappings.put(declaredAnnotation.value(), pos);
		else throw new RuntimeException("No DashObject annotation for " + tag.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public <R, D extends Dashable<R>> AbstractWriteChunk<R, D> getChunk(Class<D> dashType) {
		return (AbstractWriteChunk<R, D>) chunks[dashTag2chunkMappings.getByte(dashType)];
	}

	public <R, D extends Dashable<R>> int addDirect(AbstractWriteChunk<R, D> chunk, R object) {
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

		var chunk = (AbstractWriteChunk<R, ?>) chunks[chunkPos];
		final var objectPos = chunk.add(object);
		final int pointer = createPointer(objectPos, chunkPos);
		((Object2IntMap<R>) dedup).put(object, pointer);
		return pointer;
	}
}
