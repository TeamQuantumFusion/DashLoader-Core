package dev.quantumfusion.dashloader.core.registry;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.chunk.data.AbstractDataChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.AbstractWriteChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.FloatingWriteChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.StagedWriteChunk;
import dev.quantumfusion.dashloader.core.registry.chunk.write.WriteChunk;
import dev.quantumfusion.dashloader.core.registry.factory.DashFactory;

import java.util.*;
import java.util.function.Function;

public final class RegistryHandler<R, D extends Dashable<R>> {
	private final Collection<DashObjectClass<R, D>> dashObjects;
	private final Map<Class<?>, DashFactory.FailCallback<R, D>> callbacks = new HashMap<>();

	public RegistryHandler(Collection<DashObjectClass<R, D>> dashObjects) {
		this.dashObjects = dashObjects;
	}

	private static <O> List<O> calculateBuildOrder(List<Holder<O>> elements) {
		final int elementsSize = elements.size();
		final var mapping = new HashMap<Class<?>, Holder<O>>();

		for (var element : elements)
			mapping.put(element.self, element);

		for (var element : elements) {
			for (var dependency : element.dependencies)
				mapping.get(dependency).references++;
		}

		var queue = new ArrayDeque<Holder<O>>(elementsSize);
		for (var element : elements) {
			if (mapping.get(element.self).references == 0) queue.offer(element);
		}

		int currentPos = 0;
		var outArray = new Holder[elementsSize];
		while (!queue.isEmpty()) {
			var element = queue.poll();
			outArray[currentPos++] = element;
			for (var dependency : element.dependencies) {
				if (--mapping.get(dependency).references == 0)
					queue.offer(mapping.get(dependency));

			}
		}

		if (currentPos != elementsSize)
			throw new IllegalArgumentException("Dependency overflow! Meaning it's https://www.youtube.com/watch?v=PGNiXGX2nLU.");

		//invert and make list
		List<O> out = new ArrayList<>(outArray.length);
		//noinspection unchecked
		for (Holder<O> holder : outArray) {
			out.add(0, holder.object);
		}

		return out;
	}

	public void addCallback(Class<D> dashTag, DashFactory.FailCallback<R, D> callback) {
		this.callbacks.put(dashTag, callback);
	}

	public RegistryReader createReader(ChunkHolder... holders) {
		var dataChunks = new ArrayList<AbstractDataChunk<?, ?>>();
		for (var holder : holders) {
			Collections.addAll(dataChunks, holder.getChunks());
		}
		AbstractDataChunk<?, ?>[] out = new AbstractDataChunk[dataChunks.size()];
		for (AbstractDataChunk<?, ?> dataChunk : dataChunks) {
			out[dataChunk.pos] = dataChunk;
		}
		return new RegistryReader(out);

	}

	public RegistryWriter createWriter() {
		Map<Class<?>, DashObjectGroup> groups = new HashMap<>();
		for (DashObjectClass<R, D> dashObject : dashObjects) {
			//noinspection unchecked
			var group = groups.computeIfAbsent(dashObject.getTag(), aClass -> new DashObjectGroup((Class<D>) aClass));
			group.addDashObject(dashObject);
		}


		List<DashObjectGroup> groupOrder = calculateBuildOrder(Holder.map(groups.values(), objectGroup -> {
			objectGroup.clearInternalReferences();
			return new Holder<>(objectGroup.dashTag, objectGroup.dependencies, objectGroup);
		}));


		//noinspection unchecked
		AbstractWriteChunk<R, D>[] chunks = new AbstractWriteChunk[groupOrder.size()];
		RegistryWriter writer = new RegistryWriter(chunks);

		if (groupOrder.size() > 63)
			throw new RuntimeException("Hit group limit of 63. Please contact QuantumFusion if you hit this limit!");

		for (int i = 0; i < groupOrder.size(); i++) {
			final DashObjectGroup group = groupOrder.get(i);
			chunks[i] = group.createWriteChunk((byte) i, writer);
			writer.addChunkMapping(group.dashTag, (byte) i);
		}

		writer.compileMappings();
		return writer;
	}

	private static class Holder<O> {
		public final O object;
		private final Class<?> self;
		private final Collection<Class<?>> dependencies;
		private int references = 0;

		public Holder(Class<?> self, Collection<Class<?>> dependencies, O object) {
			this.self = self;
			this.dependencies = dependencies;
			this.object = object;
		}

		public static <O> List<Holder<O>> map(Collection<O> objects, Function<O, Holder<O>> mapper) {
			List<Holder<O>> holders = new ArrayList<>();
			objects.forEach(object -> holders.add(mapper.apply(object)));
			return holders;
		}
	}

	private final class DashObjectGroup {
		private final Class<D> dashTag;
		// enforce list on stagedWriter and force collection here to prevent this used when sorted build order is required.
		private final Collection<DashObjectClass<R, D>> dashObjects;
		private final Set<Class<?>> dependencies;
		private boolean internalReferences = false;


		private DashObjectGroup(Class<D> dashTag, Collection<DashObjectClass<R, D>> dashObjects, Set<Class<?>> dependencies) {
			this.dashTag = dashTag;
			this.dashObjects = dashObjects;
			this.dependencies = dependencies;
		}

		public DashObjectGroup(Class<D> dashTag) {
			this(dashTag, new ArrayList<>(), new HashSet<>());
		}

		public void addDashObject(DashObjectClass<R, D> dashObject) {
			dashObjects.add(dashObject);
			dependencies.addAll(dashObject.getDependencies());
		}

		public void clearInternalReferences() {
			// remove internal references and check if there are any
			for (DashObjectClass<R, D> group : dashObjects) {
				this.internalReferences |= dependencies.remove(group.getDashClass());
			}
		}

		public AbstractWriteChunk<R, D> createWriteChunk(byte pos, RegistryWriter writer) {
			var callback = callbacks.getOrDefault(dashTag, (raw, writer1) -> {
				throw new RuntimeException("Cannot create " + raw);
			});

			DashFactory<R, D> factory = DashFactory.create(dashObjects, callback);

			var name = dashTag.getSimpleName();
			if (internalReferences) {
				var groupsSorted = calculateBuildOrder(Holder.map(dashObjects, obj -> new Holder<>(obj.getDashClass(), obj.getDependencies(), obj)));
				return new StagedWriteChunk<>(pos, name, writer, groupsSorted, factory);
			} else {
				if (dependencies.size() == 0) {
					return new FloatingWriteChunk<>(pos, name, writer, dashObjects, factory);
				} else {
					return new WriteChunk<>(pos, name, writer, dashObjects, factory);
				}
			}
		}
	}
}
