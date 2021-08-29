package net.oskarstrom.dashloader.api.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.annotations.DashObject;
import net.oskarstrom.dashloader.api.annotations.Dependencies;
import net.oskarstrom.dashloader.api.annotations.RegistryTag;
import net.oskarstrom.dashloader.api.registry.storage.MultiRegistryStorage;
import net.oskarstrom.dashloader.api.registry.storage.RegistryStorage;
import net.oskarstrom.dashloader.api.registry.storage.SoloRegistryStorage;
import net.oskarstrom.dashloader.core.registry.DashRegistryImpl;
import net.oskarstrom.dashloader.core.registry.FactoryConstructorImpl;

import java.util.*;
import java.util.function.BiFunction;

public class DashRegistryBuilder {
	private final List<Class<?>> entries;
	private final Object2ObjectMap<Class<?>, Class<?>> forcedTags = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<Class<?>, CustomStorageSupplier> customFactoryStorages = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<DashRegistryImpl.ExplicitMatcher, Class<?>> explicitMappings = new Object2ObjectOpenHashMap<>();
	private BiFunction<Object, DashRegistry, Integer> failedFunc = (obj, registry) -> {
		throw new IllegalStateException("No storage was found for " + obj.getClass().getSimpleName());
	};


	public DashRegistryBuilder(List<Class<?>> entries) {
		this.entries = entries;
	}

	public static DashRegistryBuilder create() {
		return new DashRegistryBuilder(new ArrayList<>());
	}

	private static ClassEntry<?, ?>[] createBuildOrder(List<ClassEntry<?, ?>> elements) {
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

	public static <F, D extends Dashable<F>> FactoryConstructor<F, D> createConstructor(Class<?> rawClass, Class<?> dashClass) {
		try {
			//noinspection unchecked
			return FactoryConstructorImpl.createConstructor((Class<F>) rawClass, (Class<D>) dashClass);
			//TODO error handling
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	private <F, D extends Dashable<F>> ClassEntry<F, D> createClassEntry(Class<?> dash) {
		//noinspection unchecked
		return ClassEntry.create((Class<D>) dash, forcedTags);
	}

	public DashRegistry build() {

		//create classentries
		List<ClassEntry<?, ?>> classEntries = new ArrayList<>();
		for (Class<?> entry : entries) {
			final ClassEntry<Object, Dashable<Object>> classEntry = createClassEntry(entry);
			classEntries.add(classEntry);
		}

		//group entries to their tag using Kahn's algorithm
		final ClassEntry<?, ?>[] buildOrder = createBuildOrder(classEntries);


		//group entries to their tag
		Map<Class<?>, StorageMetadata> mappedEntries = new LinkedHashMap<>();
		for (int i = 0, buildOrderLength = buildOrder.length; i < buildOrderLength; i++) {
			final ClassEntry<?, ?> classEntry = buildOrder[i];
			mappedEntries.computeIfAbsent(classEntry.tag, (l) -> new StorageMetadata(classEntry.tag)).add(classEntry, i);
		}

		//sort and group on the lowest priority
		final ArrayList<StorageMetadata> sortedResults = new ArrayList<>(mappedEntries.values());
		sortedResults.sort(Comparator.comparingInt(value -> value.maxPriority));


		DashRegistry registry = new DashRegistryImpl(new Object2ByteOpenHashMap<>(), explicitMappings, failedFunc);

		for (int i = 0; i < sortedResults.size(); i++) {
			StorageMetadata sortedResult = sortedResults.get(i);
			sortedResult.compileType();
			final CustomStorageSupplier customStorageSupplier = customFactoryStorages.get(sortedResult.tag);
			RegistryStorage<?> storage;
			if (customStorageSupplier == null) {
				storage = sortedResult.createStorage(registry, i);
			} else {
				storage = customStorageSupplier.create(registry, sortedResult.type, sortedResult.classes, i);
			}
			final byte registryPointer = registry.addStorage(storage);
			for (ClassEntry<?, ?> aClass : sortedResult.classes) {
				registry.addMapping(aClass.targetClass, registryPointer);
			}
		}

		return registry;
	}

	public DashRegistryBuilder withDashObject(Class<? extends Dashable<?>> dash) {
		entries.add(dash);
		return this;
	}

	public DashRegistryBuilder withExplicitMather(DashRegistryImpl.ExplicitMatcher mather, Class<?> target) {
		explicitMappings.put(mather, target);
		return this;
	}

	public DashRegistryBuilder withFailedFunc(BiFunction<Object, DashRegistry, Integer> failedFunc) {
		this.failedFunc = failedFunc;
		return this;
	}

	public DashRegistryBuilder withCustomConstructor(Class<?> tag, CustomStorageSupplier constructor) {
		customFactoryStorages.put(tag, constructor);
		return this;
	}

	public DashRegistryBuilder withDashObject(Class<? extends Dashable<?>> dash, Class<?> forcedTag) {
		entries.add(dash);
		forcedTags.put(dash, forcedTag);
		return this;
	}

	@SafeVarargs
	public final DashRegistryBuilder withDashObjects(Class<? extends Dashable<?>>... dashes) {
		entries.addAll(Arrays.asList(dashes));
		return this;
	}

	@SafeVarargs
	public final DashRegistryBuilder addTags(Class<?> forcedTag, Class<? extends Dashable<?>>... dashes) {
		for (Class<? extends Dashable<?>> dash : dashes) {
			forcedTags.put(dash, forcedTag);
		}
		return this;
	}

	public final DashRegistryBuilder addTag(Class<?> forcedTag, Class<? extends Dashable<?>> dash) {
		forcedTags.put(dash, forcedTag);
		return this;
	}

	private enum Type {
		SOLO,
		MULTI,
		MULTISTAGE
	}

	@FunctionalInterface
	public interface CustomStorageSupplier {
		RegistryStorage<?> create(DashRegistry registry, Type type, List<ClassEntry<?, ?>> classes, int priority);
	}

	public static class ClassEntry<F, D extends Dashable<F>> {
		private final Class<D> dashClass;
		private final Class<F> targetClass;
		private final Class<?> tag;
		private final Class<Dashable<?>>[] dependencies;
		private int referenceCount = 0;

		public ClassEntry(Class<D> dashClass, Class<F> targetClass, Class<?> tag, Class<Dashable<?>>[] dependencies) {
			this.dashClass = dashClass;
			this.targetClass = targetClass;
			this.tag = tag;
			this.dependencies = dependencies;
		}

		public static <F, D extends Dashable<F>> ClassEntry<F, D> create(Class<D> dashClass, Map<Class<?>, Class<?>> forcedTags) {
			final Class<F> targetClass = getTargetClass(dashClass);
			final Class<Dashable<?>>[] dependencies = getDependencies(dashClass);
			return new ClassEntry<>(dashClass, targetClass, getTagOrDefault(dashClass, forcedTags.get(dashClass)), dependencies);
		}

		private static <F, D extends Dashable<F>> Class<F> getTargetClass(Class<? extends D> dashClass) {
			final DashObject dashMetadata = dashClass.getDeclaredAnnotation(DashObject.class);
			if (dashMetadata == null)
				throw new MissingFormatArgumentException("Missing @DashObject annotation on " + dashClass.getSimpleName());

			//noinspection unchecked
			return (Class<F>) dashMetadata.value();
		}

		private static <F, D extends Dashable<F>> Class<Dashable<?>>[] getDependencies(Class<? extends D> dashClass) {
			final Dependencies dashMetadata = dashClass.getDeclaredAnnotation(Dependencies.class);

			if (dashMetadata == null) {
				//noinspection unchecked
				return (Class<Dashable<?>>[]) new Class[0];
			}

			final Class<?>[] dependencies = dashMetadata.value();

			for (Class<?> dependency : dependencies) {
				if (Arrays.stream(dependency.getInterfaces()).noneMatch(Dashable.class::isAssignableFrom)) {
					throw new IllegalArgumentException(dashClass.getSimpleName() + " dependency \"" + dependency.getName() + "\" is not a DashObject.");
				}
			}

			//noinspection unchecked
			return (Class<Dashable<?>>[]) dependencies;
		}

		private static <F, D extends Dashable<F>> Class<?> getTagOrDefault(Class<? extends D> dashClass, Class<?> defaultClass) {
			final RegistryTag registryTag = dashClass.getDeclaredAnnotation(RegistryTag.class);
			if (registryTag == null) {
				if (defaultClass == null) {
					return dashClass;
				}
				return defaultClass;
			}
			return registryTag.value();
		}
	}

	private static class StorageMetadata {
		private final List<ClassEntry<?, ?>> classes = new ArrayList<>();
		private final Class<?> tag;
		private Type type = Type.SOLO;
		private int maxPriority = Integer.MIN_VALUE;

		public StorageMetadata(Class<?> tag) {
			this.tag = tag;
		}


		public void add(ClassEntry<?, ?> classEntry, int priority) {
			if (priority > this.maxPriority)
				this.maxPriority = priority;
			classes.add(classEntry);
		}


		public <F, D extends Dashable<F>> RegistryStorage<F> createStorage(DashRegistry registry, int priority) {


			return switch (type) {
				case SOLO -> {
					//noinspection unchecked
					final ClassEntry<F, D> classEntry = (ClassEntry<F, D>) classes.get(0);
					final FactoryConstructor<F, D> constructor = createConstructor(classEntry.dashClass, classEntry.targetClass);
					yield new SoloRegistryStorage<>(constructor, registry, priority);
				}
				case MULTI, MULTISTAGE -> {
					Object2ObjectMap<Class<F>, FactoryConstructor<F, D>> map = new Object2ObjectOpenHashMap<>();
					for (ClassEntry<?, ?> aClass : classes) {
						//noinspection unchecked
						map.put((Class<F>) aClass.targetClass, createConstructor(aClass.dashClass, aClass.targetClass));
					}
					yield new MultiRegistryStorage<>(map, registry, priority, type == Type.MULTI);
				}
			};
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

