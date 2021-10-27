package net.oskarstrom.dashloader.core.registry;

import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;

import java.util.Arrays;
import java.util.Map;
import java.util.MissingFormatArgumentException;

public class ClassEntry<F, D extends Dashable<F>> {
	public final Class<D> dashClass;
	public final Class<F> targetClass;
	public final Class<?> dInterface;
	public final Class<Dashable<?>>[] dependencies;
	public int referenceCount = 0;

	public ClassEntry(Class<D> dashClass, Class<F> targetClass, Class<?> dInterface, Class<Dashable<?>>[] dependencies) {
		this.dashClass = dashClass;
		this.targetClass = targetClass;
		this.dInterface = dInterface;
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
