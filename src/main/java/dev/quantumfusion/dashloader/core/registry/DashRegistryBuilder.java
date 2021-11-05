package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.DashObjectMetadata;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractChunkWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.write.impl.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DashRegistryBuilder {
	public static DashRegistryReader createReader(ChunkDataHolder... holders) {
		List<AbstractDataChunk<?, ?>> dataChunks = new ArrayList<>();
		for (ChunkDataHolder holder : holders) {
			dataChunks.addAll(holder.getChunks());
		}
		AbstractDataChunk<?, ?>[] out = new AbstractDataChunk[dataChunks.size()];
		for (AbstractDataChunk<?, ?> dataChunk : dataChunks) {
			out[dataChunk.pos] = dataChunk;
		}
		return new DashRegistryReader(out);
	}

	public static DashRegistryWriter createWriter(List<DashObjectMetadata<?, ? extends Dashable<?>>> classEntries, Map<Class<?>, WriteFailCallback<?, ?>> callbacks) {
		var typeMappings = new LinkedHashMap<Class<?>, List<Class<? extends Dashable<?>>>>();
		classEntries.forEach(d -> typeMappings.computeIfAbsent(d.dashType, aClass -> new ArrayList<>()).add(d.dashClass));

		var chunks = new ArrayList<ReferenceType<DashObjectHolder<?, ? extends Dashable<?>>>>();
		for (var classEntry : classEntries) {
			DashObjectHolder<?, ? extends Dashable<?>> info = new DashObjectHolder<>(classEntry);
			for (var dependency : classEntry.dependencies)
				info.dependencies.addAll(typeMappings.getOrDefault(dependency, Collections.singletonList(dependency)));

			chunks.add(info.create());
		}


		// Sort entries by dependencies
		final ReferenceType<DashObjectHolder<?, ? extends Dashable<?>>>[] meta = calculateBuildOrder(chunks);

		List<ChunkInfo<?, ? extends Dashable<?>>> infos = new ArrayList<>();
		for (Class<?> aClass : typeMappings.keySet()) {
			infos.add(new ChunkInfo(aClass, meta));
		}

		infos.sort(Comparator.comparingInt(value -> value.priority));
		return createDashRegistry(infos, callbacks);
	}

	@NotNull
	private static DashRegistryWriter createDashRegistry(List<ChunkInfo<?, ? extends Dashable<?>>> infos, Map<Class<?>, WriteFailCallback<?, ?>> callbacks) {
		AbstractChunkWriter<?, ?>[] chunks = new AbstractChunkWriter[infos.size()];
		DashRegistryWriter writer = new DashRegistryWriter(chunks);

		for (int i = 0; i < infos.size(); i++) {
			ChunkInfo<?, ? extends Dashable<?>> dashObjectHolder = infos.get(i);
			chunks[i] = dashObjectHolder.compile(writer, (byte) i, callbacks);
			writer.addChunkMapping(dashObjectHolder.dashType, (byte) i);
		}

		writer.compileMappings();
		return writer;
	}

	private static <O> ReferenceType<O>[] calculateBuildOrder(List<ReferenceType<O>> elements) {
		final int elementsSize = elements.size();
		final var mapping = new HashMap<Class<?>, ReferenceType<O>>();

		for (var element : elements)
			mapping.put(element.self, element);

		for (var element : elements) {
			for (var dependency : element.dependencies)
				mapping.get(dependency).references++;
		}

		var queue = new ArrayDeque<ReferenceType<O>>(elementsSize);
		for (var element : elements) {
			if (mapping.get(element.self).references == 0) queue.offer(element);
		}

		int currentPos = 0;
		var out = new ReferenceType[elementsSize];
		while (!queue.isEmpty()) {
			var element = queue.poll();
			out[currentPos++] = element;
			for (var dependency : element.dependencies) {
				if (--mapping.get(dependency).references == 0)
					queue.offer(mapping.get(dependency));

			}
		}

		if (currentPos != elementsSize)
			throw new IllegalArgumentException("Dependency overflow! Meaning it's https://www.youtube.com/watch?v=PGNiXGX2nLU.");

		//invert
		for (int left = 0, right = out.length - 1; left < right; left++, right--) {
			var temp = out[left];
			out[left] = out[right];
			out[right] = temp;
		}

		return out;
	}

	private static class ReferenceType<O> {
		private final O object;
		private final Class<?> self;
		private final List<Class<?>> dependencies;
		private int references = 0;

		public ReferenceType(O object, Class<?> self, List<Class<?>> dependencies) {
			this.object = object;
			this.self = self;
			this.dependencies = dependencies;
		}
	}

	public static class DashObjectHolder<R, D extends Dashable<R>> {
		private final DashObjectMetadata<R, D> metadata;
		private final List<Class<?>> dependencies = new ArrayList<>();

		public DashObjectHolder(DashObjectMetadata<R, D> metadata) {
			this.metadata = metadata;
		}

		public ReferenceType<DashObjectHolder<?, ? extends Dashable<?>>> create() {
			return new ReferenceType<>(this, metadata.dashClass, dependencies);
		}
	}

	public static class ChunkInfo<R, D extends Dashable<R>> {
		public final Class<?> dashType;
		public final List<DashObjectMetadata<R, D>> dashMetas = new ArrayList<>();
		private int priority = Integer.MIN_VALUE;

		public ChunkInfo(Class<?> dashType, ReferenceType<DashObjectHolder<R, D>>[] dashMetas) {
			this.dashType = dashType;
			for (int i = 0; i < dashMetas.length; i++) {
				final ReferenceType<DashObjectHolder<R, D>> dashObject = dashMetas[i];
				if (dashObject.object.metadata.dashType == dashType) {
					this.dashMetas.add(dashObject.object.metadata);
					this.priority = i;
				}
			}
		}

		public boolean anyInternalReferences() {
			for (var dashObject : dashMetas) {
				for (var dependency : dashObject.dependencies) {
					for (var object : dashMetas) {
						if (dependency == object.dashClass) return true;
					}
				}
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		public AbstractChunkWriter<R, D> compile(DashRegistryWriter writer, byte pos, Map<Class<?>, WriteFailCallback<?, ?>> callbacks) {
			var floating = dashMetas.stream().allMatch(metadata -> metadata.dependencies.length == 0);
			var callback = (WriteFailCallback<R, D>) callbacks.getOrDefault(dashType, (WriteFailCallback<R, D>) (craw, cwriter) -> {
				throw new RuntimeException("Cannot write " + craw.getClass().getSimpleName() + " in a " + dashType.getSimpleName() + " writer");
			});

			if (dashMetas.size() == 1) {
				var meta = (DashObjectMetadata<R, D>) dashMetas.get(0);
				if (floating) return FloatingSingleChunkWriter.create(pos, writer, callback, meta);
				else return SingleChunkWriter.create(pos, writer, callback, meta);
			}

			if (anyInternalReferences()) {
				return StagedMultiChunkWriter.create(pos, writer, callback, dashMetas, dashType);
			} else {
				if (floating) return FloatingMultiChunkWriter.create(pos, writer, callback, dashMetas, dashType);
				else return MultiChunkWriter.create(pos, writer, callback, dashMetas, dashType);
			}

		}
	}


}
