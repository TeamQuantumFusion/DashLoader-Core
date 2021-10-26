package net.oskarstrom.dashloader.core.registry;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;
import net.oskarstrom.dashloader.core.registry.storage.MultiRegistryStorage;
import net.oskarstrom.dashloader.core.registry.storage.MultiStageRegistryStorage;
import net.oskarstrom.dashloader.core.registry.storage.RegistryStorage;
import net.oskarstrom.dashloader.core.registry.storage.SoloRegistryStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class DashRegistryBuilder {
	private final List<Class<?>> entries;
	private final Object2ObjectMap<Class<?>, Class<?>> forcedTags = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<Class<?>, CustomStorageSupplier> customFactoryStorages = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<DashRegistry.ExplicitMatcher, Class<?>> explicitMappings = new Object2ObjectOpenHashMap<>();
	private BiFunction<Object, DashRegistry, Integer> failedFunc = (obj, registry) -> {
		System.out.println("No storage was found for " + obj.getClass().getSimpleName());
		return -1;
	};

	private DashRegistryBuilder(List<Class<?>> entries) {
		this.entries = entries;
	}

	/**
	 * <h1>	create</h1>
	 * <h3>/wierd characters that are not utf-8/</h3>
	 * <h2>1. bring (something) into existence.</h2>
	 * <h4>"he created a thirty-acre lake"</h4>
	 * <h2>2. make a fuss; complain.</h2>
	 * <h4>(UK) "little kids create because they hate being ignored"</h4>
	 *
	 * @return the fuss
	 */
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

	public static <F, D extends Dashable<F>> FactoryConstructor<F, D> createConstructor(Class<?> dashClass, Class<?> rawClass) {
		try {
			//noinspection unchecked
			return FactoryConstructor.createConstructor((Class<F>) rawClass, (Class<D>) dashClass);
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

	/**
	 * Build the DashRegistry
	 * <h2>
	 * please explode this builder and never use it again, gc told me it really hates builders.
	 * </h2>
	 *
	 * @return The DashRegistry.
	 */
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


		final Object2ByteOpenHashMap<Class<?>> tags = new Object2ByteOpenHashMap<>();
		DashRegistry registry = new DashRegistry(new Object2ByteOpenHashMap<>(), explicitMappings, failedFunc, tags);

		for (int i = 0; i < sortedResults.size(); i++) {
			StorageMetadata sortedResult = sortedResults.get(i);
			sortedResult.compileType();
			final CustomStorageSupplier customStorageSupplier = customFactoryStorages.get(sortedResult.tag);
			RegistryStorage<?> storage;
			if (customStorageSupplier == null) {
				storage = sortedResult.createStorage(registry);
			} else {
				final RegistryStorage<?> customStorage = customStorageSupplier.create(registry, sortedResult.type, sortedResult.classes, i);
				// if returns null use default
				if (customStorage == null) {
					storage = sortedResult.createStorage(registry);
				} else {
					storage = customStorage;
				}
			}
			final byte registryPointer = registry.addStorage(storage);
			for (ClassEntry<?, ?> aClass : sortedResult.classes) {
				registry.addMapping(aClass.targetClass, registryPointer);
			}
			System.out.println(sortedResult.tag.getSimpleName() + " | " + i + " / " + registryPointer);
			tags.put(sortedResult.tag, registryPointer);
		}

		return registry;
	}

	/**
	 * Add a DashObject to be registered.
	 *
	 * @param dashObject The DashObject Class
	 * @return this builder for chaining
	 */
	public DashRegistryBuilder withDashObject(Class<? extends Dashable<?>> dashObject) {
		entries.add(dashObject);
		return this;
	}

	/**
	 * Add a DashObject to be registered. With a forced tag
	 *
	 * @param dashObject The DashObject Class
	 * @param forcedTag  The Tag to be forced on
	 * @return this builder for chaining
	 */
	public DashRegistryBuilder withDashObject(Class<? extends Dashable<?>> dashObject, Class<?> forcedTag) {
		entries.add(dashObject);
		forcedTags.put(dashObject, forcedTag);
		return this;
	}

	/**
	 * Add multiple DashObjects
	 *
	 * @param dashObjects The DashObjects
	 * @return this builder for chaining
	 */
	@SafeVarargs
	public final DashRegistryBuilder withDashObjects(Class<? extends Dashable<?>>... dashObjects) {
		entries.addAll(Arrays.asList(dashObjects));
		return this;
	}

	/**
	 * Force a tag on multiple DashObject's at a time
	 *
	 * @param forcedTag   The tag to be forced
	 * @param dashObjects The DashObjects
	 * @return this builder for chaining
	 */
	@SafeVarargs
	public final DashRegistryBuilder addTags(Class<?> forcedTag, Class<? extends Dashable<?>>... dashObjects) {
		for (Class<? extends Dashable<?>> dash : dashObjects) {
			forcedTags.put(dash, forcedTag);
		}
		return this;
	}

	/**
	 * Force a tag on a DashObject
	 *
	 * @param forcedTag  The tag to be forced
	 * @param dashObject The DashObject
	 * @return this builder for chaining
	 */
	public final DashRegistryBuilder addTag(Class<?> forcedTag, Class<? extends Dashable<?>> dashObject) {
		forcedTags.put(dashObject, forcedTag);
		return this;
	}

	/**
	 * If the registry fails this will run, Do your exceptions here.
	 *
	 * @param failedFunc The fail function.
	 * @return this builder for chaining
	 */
	public DashRegistryBuilder withFailedFunc(BiFunction<Object, DashRegistry, Integer> failedFunc) {
		this.failedFunc = failedFunc;
		return this;
	}

	/**
	 * Add an explicit matcher. If mappings fail it will check with explicit matchers if anything matches.
	 *
	 * @param matcher The matching function.
	 * @param target  If the matcher returns true, Search for this class in mappings.
	 * @return this builder for chaining
	 */
	public DashRegistryBuilder withExplicitMather(DashRegistry.ExplicitMatcher matcher, Class<?> target) {
		explicitMappings.put(matcher, target);
		return this;
	}

	/**
	 * Add a custom RegistryStorage, Everything with this tag will use this constructor.
	 *
	 * @param tag         Anything with this tag will use this storage.
	 * @param constructor The Creator
	 * @return this builder for chaining
	 */
	public DashRegistryBuilder withCustomConstructor(Class<?> tag, CustomStorageSupplier constructor) {
		customFactoryStorages.put(tag, constructor);
		return this;
	}

	public enum Type {
		SOLO,
		MULTI,
		MULTISTAGE
	}

	@FunctionalInterface
	public interface CustomStorageSupplier {
		/**
		 * A custom registry constructor
		 *
		 * @param registry The DashRegistry in charge
		 * @param type     The intended type
		 * @param classes  The current classes that are about to get mapped.
		 * @param priority The priority in deserialization.
		 * @return The RegistryStorage. Return null if you want to use the default.
		 */
		@Nullable RegistryStorage<?> create(DashRegistry registry, Type type, List<ClassEntry<?, ?>> classes, int priority);
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


		public <F, D extends Dashable<F>> RegistryStorage<F> createStorage(DashRegistry registry) {


			return switch (type) {
				case SOLO -> {
					//noinspection unchecked
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

