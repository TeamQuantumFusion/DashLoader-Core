package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.api.DashDependencies;
import dev.quantumfusion.dashloader.core.api.DashObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * A DashObject which is an object with adds Dash support to a target object. <br>
 * This class is very lazy as reflection is really slow
 *
 * @param <R> Raw
 * @param <D> Dashable
 */
public final class DashObjectClass<R, D extends Dashable<R>> {
	private final Class<D> dashClass;

	@Nullable
	private Class<R> targetClass;
	@Nullable
	private Class<? extends Dashable<?>> dashTag;
	@Nullable
	private List<Class<?>> dependencies;

	public DashObjectClass(Class<D> dashClass) {
		this.dashClass = dashClass;
	}

	public Class<D> getDashClass() {
		return dashClass;
	}

	// lazy
	@NotNull
	public Class<R> getTargetClass() {
		if (targetClass == null) {
			var dashObjectAnnotation = dashClass.getDeclaredAnnotation(DashObject.class);
			if (dashObjectAnnotation == null)
				throw new RuntimeException("Registered Class " + dashClass.getSimpleName() + " does not have a @DashObject annotation.");
			targetClass = (Class<R>) dashObjectAnnotation.value();
		}
		return targetClass;
	}

	// lazy
	@NotNull
	public Class<? extends Dashable<?>> getTag() {
		if (dashTag == null) {
			Class<? extends Dashable<?>> dashInterface = null;
			for (Class<?> anInterface : dashClass.getInterfaces()) {
				if (Dashable.class.isAssignableFrom(anInterface)) {
					dashInterface = (Class<? extends Dashable<?>>) anInterface;
					break;
				}
			}

			if (dashInterface == null)
				throw new RuntimeException(dashClass.getSimpleName() + " does not have an interface that inherits Dashable");


			//noinspection RedundantCast // very required
			this.dashTag = (dashInterface == ((Class<? extends Dashable>) Dashable.class) ? dashClass : dashInterface);
		}
		return dashTag;
	}

	// lazy
	@NotNull
	public List<Class<?>> getDependencies() {
		if (dependencies == null) {
			Class<?>[] dependencies;
			var dependenciesAnnotation = dashClass.getDeclaredAnnotation(DashDependencies.class);
			if (dependenciesAnnotation == null) dependencies = new Class[0];
			else dependencies = dependenciesAnnotation.value();

			for (Class<?> dependency : dependencies) {
				if (dependency.getDeclaredAnnotation(DashObject.class) == null) {
					throw new RuntimeException(
							"Registered Class " + dashClass.getSimpleName() + " Dependency \"" + dependency.getSimpleName() +
									"\" does not have a @DashObject annotation and therefore is not a DashObject");
				}
			}
			this.dependencies = List.of(dependencies);
		}
		return dependencies;
	}
}
