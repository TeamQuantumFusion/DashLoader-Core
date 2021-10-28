package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.api.annotation.DashDependencies;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.ChunkWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.write.MultiChunkWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.write.SoloChunkWriter;
import dev.quantumfusion.dashloader.core.registry.chunk.write.StagedChunkWriter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DashRegistryBuilder {

	public static DashRegistryReader createReader(ChunkDataHolder... holders) {
		List<AbstractDataChunk<?, ?>> dataChunks = new ArrayList<>();
		for (ChunkDataHolder holder : holders) {
			dataChunks.addAll(holder.getChunks());
		}
		return new DashRegistryReader(dataChunks.toArray(AbstractDataChunk[]::new));
	}

	@SafeVarargs
	public static DashRegistryWriter createWriter(Class<Dashable<?>>... dashables) {
		// Compile class entries
		var classEntries = new ArrayList<DashObjectMetadata<?, ?>>();
		for (var entry : dashables)
			classEntries.add(DashObjectMetadata.create(entry));

		// Sort entries by dependencies
		var buildOrder = calculateBuildOrder(classEntries);

		// Create StorageMetadata from entries sharing the same DashInterface
		var mappedEntries = new LinkedHashMap<Class<?>, ChunkMetadata>();
		for (int pos = 0; pos < buildOrder.length; pos++) {
			var classEntry = buildOrder[pos];
			mappedEntries.computeIfAbsent(classEntry.dashType, (l) -> new ChunkMetadata(classEntry.dashType)).add(classEntry, pos);
		}

		// sort StorageMetadata dependant on their priorities
		var sortedResults = new ArrayList<>(mappedEntries.values());
		sortedResults.sort(Comparator.comparingInt(value -> value.maxPriority));
		return createDashRegistry(sortedResults);
	}

	@NotNull
	private static DashRegistryWriter createDashRegistry(ArrayList<ChunkMetadata> meta) {
		ChunkWriter<?, ?>[] chunks = new ChunkWriter[meta.size()];
		DashRegistryWriter writer = new DashRegistryWriter(chunks);
		for (int i = 0; i < meta.size(); i++) {
			var chunkMeta = meta.get(i);
			chunkMeta.compileType();
			chunks[i] = chunkMeta.createWriter(writer, (byte) i);
		}
		writer.createMappings();
		return writer;
	}

	public static DashObjectMetadata<?, ?>[] calculateBuildOrder(List<DashObjectMetadata<?, ?>> elements) {
		final int elementsSize = elements.size();
		final var mapping = new HashMap<Class<?>, DashObjectMetadata<?, ?>>();

		for (var element : elements)
			mapping.put(element.dashClass, element);

		for (var element : elements) {
			for (var dependency : element.dependencies)
				mapping.get(dependency).referenceCount++;
		}

		var queue = new ArrayDeque<DashObjectMetadata<?, ?>>(elementsSize);
		for (var element : elements) {
			if (mapping.get(element.dashClass).referenceCount == 0) queue.offer(element);
		}

		int currentPos = 0;
		var out = new DashObjectMetadata[elementsSize];
		while (!queue.isEmpty()) {
			var element = queue.poll();
			out[currentPos++] = element;
			for (var dependency : element.dependencies) {
				if (--mapping.get(dependency).referenceCount == 0)
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

	public enum Type {
		SOLO,
		MULTI,
		STAGED;
	}

	public static class ChunkMetadata {
		public final List<DashObjectMetadata<?, ?>> metas = new ArrayList<>();
		public final Class<?> tag;
		public Type type = Type.SOLO;
		public int maxPriority = Integer.MIN_VALUE;

		public ChunkMetadata(Class<?> tag) {
			this.tag = tag;
		}


		public void add(DashObjectMetadata<?, ?> dashObjectMetadata, int priority) {
			if (priority > this.maxPriority)
				this.maxPriority = priority;
			metas.add(dashObjectMetadata);
		}


		@SuppressWarnings("unchecked")
		public <R, D extends Dashable<R>> ChunkWriter<R, D> createWriter(DashRegistryWriter registry, byte pos) {
			return switch (type) {
				case SOLO -> {
					var meta = (DashObjectMetadata<R, D>) metas.get(0);
					var constructor = (DashConstructor<R, D>) DashConstructor.create(meta.dashClass, meta.targetClass);
					yield new SoloChunkWriter<>(pos, registry, meta.targetClass, constructor);
				}
				case MULTI -> {
					var mappings = new Object2ObjectOpenHashMap<Class<?>, DashConstructor<R, D>>();
					for (var meta : metas) {
						mappings.put(meta.targetClass, DashConstructor.create(meta.dashClass, meta.targetClass));
					}
					yield new MultiChunkWriter<>(pos, registry, mappings);
				}
				case STAGED -> {
					var stagesOut = new Object2ObjectOpenHashMap<Class<?>, StagedChunkWriter.StageInfo<R, D>>();
					for (int stage = 0; stage < metas.size(); stage++) {
						var meta = metas.get(stage);
						stagesOut.put(meta.targetClass, new StagedChunkWriter.StageInfo<>(DashConstructor.create(meta.dashClass, meta.targetClass), stage));
					}

					yield new StagedChunkWriter<>(pos, registry, metas.size(), stagesOut);
				}
			};
		}

		public void compileType() {
			if (metas.size() == 1) type = Type.SOLO;
			else {
				for (var meta : metas)
					for (var dependency : meta.dependencies)
						for (var dependencyMeta : metas)
							if (dependency.equals(dependencyMeta.dashClass)) {
								type = Type.STAGED;
								return;
							}

				type = Type.MULTI;
			}
		}
	}

	public static class DashObjectMetadata<F, D extends Dashable<F>> {
		public final Class<D> dashClass;
		public final Class<F> targetClass;
		public final Class<?> dashType;
		public final Class<Dashable<?>>[] dependencies;
		public int referenceCount = 0;

		public DashObjectMetadata(Class<D> dashClass, Class<F> targetClass, Class<?> dashType, Class<Dashable<?>>[] dependencies) {
			this.dashClass = dashClass;
			this.targetClass = targetClass;
			this.dashType = dashType;
			this.dependencies = dependencies;
		}


		@SuppressWarnings("unchecked")
		public static <F, D extends Dashable<F>> DashObjectMetadata<F, D> create(Class<?> rawDashClass) {
			Class<D> dashClass = (Class<D>) rawDashClass;
			var targetClass = getTargetClass(dashClass);
			var dependencies = getDependencies(dashClass);
			return new DashObjectMetadata<>(dashClass, targetClass, getDashType(dashClass), dependencies);
		}

		private static <F, D extends Dashable<F>> Class<F> getTargetClass(Class<? extends D> dashClass) {
			var dashAnnotation = dashClass.getDeclaredAnnotation(DashObject.class);
			if (dashAnnotation == null)
				throw new MissingFormatArgumentException("Missing @DashObject annotation on " + dashClass.getSimpleName());

			//noinspection unchecked
			return (Class<F>) dashAnnotation.value();
		}

		@SuppressWarnings("unchecked")
		private static <F, D extends Dashable<F>> Class<Dashable<?>>[] getDependencies(Class<? extends D> dashClass) {
			var dependencyAnnotation = dashClass.getDeclaredAnnotation(DashDependencies.class);
			if (dependencyAnnotation == null)
				return (Class<Dashable<?>>[]) new Class[0];

			var dependencies = dependencyAnnotation.value();
			for (var dependency : dependencies) {
				if (Arrays.stream(dependency.getInterfaces()).noneMatch(Dashable.class::isAssignableFrom))
					throw new IllegalArgumentException(dashClass.getSimpleName() + " dependency \"" + dependency.getName() + "\" is not a DashObject.");
			}

			//noinspection unchecked
			return (Class<Dashable<?>>[]) dependencies;
		}

		private static <F, D extends Dashable<F>> Class<?> getDashType(Class<? extends D> dashClass) {
			final Class<?> anInterface = dashClass.getInterfaces()[0];
			if (anInterface == Dashable.class) return dashClass;
			return anInterface;
		}
	}
}
