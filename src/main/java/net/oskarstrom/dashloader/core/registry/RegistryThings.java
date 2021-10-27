package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.registry.storage.MultiRegistryStorage;
import net.oskarstrom.dashloader.core.registry.storage.MultiStageRegistryStorage;
import net.oskarstrom.dashloader.core.registry.storage.RegistryStorage;
import net.oskarstrom.dashloader.core.registry.storage.SoloRegistryStorage;

import java.util.*;


//Please someone make this class not a thing
public class RegistryThings {

	public static ClassEntry<?, ?>[] calculateBuildOrder(List<ClassEntry<?, ?>> elements) {
		final int n = elements.size();
		final Map<Class<?>, ClassEntry<?, ?>> mapping = new HashMap<>();

		for (ClassEntry<?, ?> element : elements) {
			mapping.put(element.dashClass, element);
		}

		for (ClassEntry<?, ?> element : elements) {
			for (Class<?> dependency : element.dependencies) {
				mapping.get(dependency).referenceCount++;
			}
		}

		final Queue<ClassEntry<?, ?>> queue = new ArrayDeque<>(n);
		for (ClassEntry<?, ?> element : elements) {
			if (mapping.get(element.dashClass).referenceCount == 0) {
				queue.offer(element);
			}
		}

		int currentPos = 0;
		final ClassEntry<?, ?>[] out = new ClassEntry[n];
		while (!queue.isEmpty()) {
			final ClassEntry<?, ?> element = queue.poll();
			out[currentPos++] = element;
			for (Class<?> dependency : element.dependencies) {
				if (--mapping.get(dependency).referenceCount == 0)
					queue.offer(mapping.get(dependency));

			}
		}

		if (currentPos != n) {
			throw new IllegalArgumentException("Dependency overflow! Meaning it's https://www.youtube.com/watch?v=PGNiXGX2nLU.");
		}

		//invert
		for (int left = 0, right = out.length - 1; left < right; left++, right--) {
			ClassEntry<?, ?> temp = out[left];
			out[left] = out[right];
			out[right] = temp;
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	public static <F, D extends Dashable<F>> FactoryConstructor<F, D> createConstructor(Class<?> dashClass, Class<?> rawClass) {
		try {
			return FactoryConstructor.createConstructor((Class<F>) rawClass, (Class<D>) dashClass);
			//TODO error handling
		} catch (IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public enum Type {
		SOLO,
		MULTI,
		MULTISTAGE
	}

	public static class StorageMetadata {
		public final List<ClassEntry<?, ?>> classes = new ArrayList<>();
		public final Class<?> tag;
		public Type type = Type.SOLO;
		public int maxPriority = Integer.MIN_VALUE;

		public StorageMetadata(Class<?> tag) {
			this.tag = tag;
		}


		public void add(ClassEntry<?, ?> classEntry, int priority) {
			if (priority > this.maxPriority)
				this.maxPriority = priority;
			classes.add(classEntry);
		}


		@SuppressWarnings("unchecked")
		public <F, D extends Dashable<F>> RegistryStorage<F> createStorage(DashRegistry registry) {
			return switch (type) {
				case SOLO -> {
					final ClassEntry<F, D> classEntry = (ClassEntry<F, D>) classes.get(0);
					final FactoryConstructor<F, D> constructor = createConstructor(classEntry.dashClass, classEntry.targetClass);
					yield new SoloRegistryStorage<>(constructor, registry, tag);
				}
				case MULTI -> {
					Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> map = createMultiConstructors(classes);
					yield new MultiRegistryStorage<>(map, registry, tag);
				}
				case MULTISTAGE -> {
					Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> map = createMultiConstructors(classes);
					Map<Class<?>, Integer> stages = new HashMap<>();
					for (int i = 0, classesSize = classes.size(); i < classesSize; i++) {
						stages.put(classes.get(i).dashClass, i);
					}
					yield new MultiStageRegistryStorage<>(map, registry, stages, tag);
				}
			};
		}

		private <F, D extends Dashable<F>> Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> createMultiConstructors(List<ClassEntry<?, ?>> classes) {
			Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> map = new Object2ObjectOpenHashMap<>();
			for (ClassEntry<?, ?> aClass : classes) {
				//noinspection unchecked
				map.put((Class<F>) aClass.targetClass, createConstructor(aClass.dashClass, aClass.targetClass));
			}
			return map;
		}

		public void compileType() {
			if (classes.size() < 2) {
				type = Type.SOLO;
			} else {
				boolean dependencyInside = false;
				stop:
				for (ClassEntry<?, ?> aClass : classes) {
					for (Class<Dashable<?>> dependency : aClass.dependencies) {
						for (ClassEntry<?, ?> classEntry : classes) {
							if (dependency.equals(classEntry.dashClass)) {
								dependencyInside = true;
								break stop;
							}
						}
					}
				}
				if (dependencyInside) {
					type = Type.MULTISTAGE;
				} else {
					type = Type.MULTI;
				}
			}
		}
	}
}
