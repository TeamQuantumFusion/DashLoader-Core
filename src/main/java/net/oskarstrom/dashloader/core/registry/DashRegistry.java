package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.oskarstrom.dashloader.core.registry.chunk.AbstractRegistryChunk;
import net.oskarstrom.dashloader.core.registry.chunk.data.RegistryChunkData;
import org.jetbrains.annotations.Nullable;

public class DashRegistry {
	@Nullable
	private final Object2ByteMap<Class<?>> mappings;
	private final AbstractRegistryChunk<?, ?>[] chunks;

	/**
	 * This is the registry that will be created when the cache is available and when {@link DashRegistry#get(int)} will be used.
	 */
	public DashRegistry(RegistryChunkData<?, ?>[] data) {
		this.mappings = null;
		this.chunks = new AbstractRegistryChunk[data.length];

		for (int i = 0; i < data.length; i++) chunks[i] = data[i].export();
	}

	/**
	 * This is the registry that will be created when the cache is not available and when {@link DashRegistry#add(Object)} will be used.
	 */
	public DashRegistry(AbstractRegistryChunk<?, ?>[] chunks) {
		this.mappings = new Object2ByteOpenHashMap<>();
		this.chunks = chunks;

		for (int i = 0; i < chunks.length; i++) {
			for (Class<?> aClass : chunks[i].getClasses()) mappings.put(aClass, (byte) i);
		}
	}

	public static int createPointer(int objectPos, byte chunkPos) {
		if (chunkPos > 0x3f) throw new IllegalStateException("Chunk pos is too big. " + chunkPos + " > " + 0x3f);
		if (objectPos > 0x3ffffff)
			throw new IllegalStateException("Object pos is too big. " + objectPos + " > " + 0x3ffffff);
		return objectPos << 6 | (chunkPos & 0x3f);
	}

	public static int getObjectPos(int pointer) {
		return pointer >>> 6;
	}

	public static byte getChunkPos(int pointer) {
		return (byte) (pointer & 0x3f);
	}

	@SuppressWarnings("unchecked")
	public <R> int add(R object) {
		if (mappings == null) throw new RuntimeException("Used add on Undash registry.");

		final var targetClass = object.getClass();
		final var chunk = (AbstractRegistryChunk<R, ?>) chunks[mappings.getByte(targetClass)];
		final var objectPos = chunk.add(object);
		return createPointer(objectPos, chunk.pos);
	}

	@SuppressWarnings("unchecked")
	public <R> R get(int pointer) {
		final var chunkPos = getChunkPos(pointer);
		final var chunk = chunks[chunkPos];
		return (R) chunk.get(getObjectPos(pointer));
	}
}
