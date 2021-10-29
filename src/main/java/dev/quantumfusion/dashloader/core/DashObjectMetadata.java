package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.api.annotation.DashDependencies;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashObjectMetadata<F, D extends Dashable<F>> {
	private static final Map<Class<?>, DashObjectMetadata<?, ?>> CACHE = new HashMap<>();

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
		if (CACHE.containsKey(rawDashClass)) return (DashObjectMetadata<F, D>) CACHE.get(rawDashClass);
		Class<D> dashClass = (Class<D>) rawDashClass;
		var targetClass = getTargetClass(dashClass);
		var dependencies = getDependencies(dashClass);
		final DashObjectMetadata<F, D> meta = new DashObjectMetadata<>(dashClass, targetClass, getDashType(dashClass), dependencies);
		CACHE.put(rawDashClass, meta);
		return meta;
	}

	private static <F, D extends Dashable<F>> Class<F> getTargetClass(Class<? extends D> dashClass) {
		var dashAnnotation = dashClass.getDeclaredAnnotation(DashObject.class);
		if (dashAnnotation == null)
			throw new RuntimeException("Missing @DashObject annotation on " + dashClass.getSimpleName());

		//noinspection unchecked
		final Class<F> rawClass = (Class<F>) dashAnnotation.value();

		if (Dashable.class.isAssignableFrom(rawClass))
			throw new RuntimeException("Target type " + rawClass.getSimpleName() + " is a Dashable in " + dashClass.getSimpleName());


		return rawClass;
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

	public String getFileName() {
		return dashType.getSimpleName().toLowerCase(Locale.ROOT);
	}
}
