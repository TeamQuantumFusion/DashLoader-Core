package net.oskarstrom.dashloader.core;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.oskarstrom.dashloader.core.registry.ClassEntry;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.registry.RegistryThings;
import net.oskarstrom.dashloader.core.registry.regdata.MultiRegistryDataImpl;
import net.oskarstrom.dashloader.core.registry.regdata.MultiStageRegistryData;
import net.oskarstrom.dashloader.core.registry.regdata.RegistryData;
import net.oskarstrom.dashloader.core.registry.regdata.SoloRegistryDataImpl;
import net.oskarstrom.dashloader.core.registry.storage.RegistryStorage;
import net.oskarstrom.dashloader.core.serializer.DashSerializerManager;
import net.oskarstrom.dashloader.core.util.ClassLoaderHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

public class DashLoaderCore {
	private final List<Class<?>> entries;
	private final Object2ObjectMap<Class<?>, Class<?>> dashClasses = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<Class<?>, CustomStorageSupplier> customFactoryStorages = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<DashRegistry.ExplicitMatcher, Class<?>> explicitMappings = new Object2ObjectOpenHashMap<>();
	private BiFunction<Object, DashRegistry, Integer> failedFunc = (obj, registry) -> {
		System.out.println("No storage was found for " + obj.getClass().getSimpleName());
		return -1;
	};

	private DashLoaderCore(List<Class<?>> entries) {
		this.entries = entries;
	}

	public static DashLoaderCore create() {
		ClassLoaderHelper.init();
		return new DashLoaderCore(new ArrayList<>());
	}

	public DashExportHandler createExportHandler(int size) {
		return new DashExportHandler(size);
	}

	public DashSerializerManager createSerializationManager(Path systemCacheFolder) {
		var manager = new DashSerializerManager(systemCacheFolder);
		manager.addSubclasses(RegistryData.class, SoloRegistryDataImpl.class, MultiRegistryDataImpl.class, MultiStageRegistryData.class);
		compileClassEntries().forEach(classEntry -> manager.addSubclass(classEntry.dInterface, classEntry.dashClass));
		return manager;
	}

	public DashRegistry createDashRegistry() {
		var classEntries = compileClassEntries();

		// Sort entries by dependencies
		var buildOrder = RegistryThings.calculateBuildOrder(classEntries);

		// Create StorageMetadata from entries sharing the same DashInterface
		var mappedEntries = new LinkedHashMap<Class<?>, RegistryThings.StorageMetadata>();
		for (int pos = 0; pos < buildOrder.length; pos++) {
			var classEntry = buildOrder[pos];
			mappedEntries.computeIfAbsent(classEntry.dInterface, (l) -> new RegistryThings.StorageMetadata(classEntry.dInterface)).add(classEntry, pos);
		}

		// sort StorageMetadata dependant on their priorities
		var sortedResults = new ArrayList<>(mappedEntries.values());
		sortedResults.sort(Comparator.comparingInt(value -> value.maxPriority));
		return createDashRegistry(sortedResults);
	}

	@SuppressWarnings("unchecked")
	private <F, D extends Dashable<F>> ClassEntry<F, D> createClassEntry(Class<?> dash) {
		return ClassEntry.create((Class<D>) dash, dashClasses);
	}

	@NotNull
	private DashRegistry createDashRegistry(ArrayList<RegistryThings.StorageMetadata> meta) {
		var dashInterfaces = new Object2ByteOpenHashMap<Class<?>>();
		var registry = new DashRegistry(new Object2ByteOpenHashMap<>(), explicitMappings, failedFunc, dashInterfaces);
		for (int i = 0; i < meta.size(); i++) {
			RegistryThings.StorageMetadata sortedResult = meta.get(i);
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
			dashInterfaces.put(sortedResult.tag, registryPointer);
		}
		return registry;
	}

	@NotNull
	private ArrayList<ClassEntry<?, ?>> compileClassEntries() {
		// Compile class entries
		var classEntries = new ArrayList<ClassEntry<?, ?>>();
		for (Class<?> entry : entries) {
			final ClassEntry<Object, Dashable<Object>> classEntry = createClassEntry(entry);
			classEntries.add(classEntry);
		}
		return classEntries;
	}

	public DashLoaderCore withDashObject(Class<? extends Dashable<?>> dashObject) {
		entries.add(dashObject);
		return this;
	}

	public DashLoaderCore withDashObject(Class<? extends Dashable<?>> dashObject, Class<?> forcedTag) {
		entries.add(dashObject);
		dashClasses.put(dashObject, forcedTag);
		return this;
	}

	@SafeVarargs
	public final DashLoaderCore withDashObjects(Class<? extends Dashable<?>>... dashObjects) {
		entries.addAll(Arrays.asList(dashObjects));
		return this;
	}

	@SafeVarargs
	public final DashLoaderCore addTags(Class<?> forcedTag, Class<? extends Dashable<?>>... dashObjects) {
		for (Class<? extends Dashable<?>> dash : dashObjects) {
			dashClasses.put(dash, forcedTag);
		}
		return this;
	}

	public final DashLoaderCore addTag(Class<?> forcedTag, Class<? extends Dashable<?>> dashObject) {
		dashClasses.put(dashObject, forcedTag);
		return this;
	}

	public DashLoaderCore withFailedFunc(BiFunction<Object, DashRegistry, Integer> failedFunc) {
		this.failedFunc = failedFunc;
		return this;
	}

	public DashLoaderCore withExplicitMather(DashRegistry.ExplicitMatcher matcher, Class<?> target) {
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
	public DashLoaderCore withCustomConstructor(Class<?> tag, CustomStorageSupplier constructor) {
		customFactoryStorages.put(tag, constructor);
		return this;
	}

	@FunctionalInterface
	public interface CustomStorageSupplier {
		@Nullable RegistryStorage<?> create(DashRegistry registry, RegistryThings.Type type, List<ClassEntry<?, ?>> classes, int priority);
	}


}

