package dev.quantumfusion.dashloader.core.registry.factory;

import dev.quantumfusion.dashloader.core.DashObjectClass;
import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.dashloader.core.registry.factory.creator.CreationError;
import dev.quantumfusion.dashloader.core.registry.factory.creator.Creator;
import dev.quantumfusion.dashloader.core.registry.factory.creator.MultiCreator;
import dev.quantumfusion.dashloader.core.registry.factory.creator.SoloCreator;

import java.util.Collection;

public final class DashFactory<R, D extends Dashable<R>> {
	private final Creator<R, D> creator;
	private final FailCallback<R, D> failCallback;

	private DashFactory(Creator<R, D> creator, FailCallback<R, D> failCallback) {
		this.creator = creator;
		this.failCallback = failCallback;
	}

	public static <R, D extends Dashable<R>> DashFactory<R, D> create(Collection<DashObjectClass<R, D>> dashObjects, FailCallback<R, D> failCallback) {
		if (dashObjects.size() > 1) {
			return new DashFactory<>(MultiCreator.create(dashObjects), failCallback);
		} else {
			return new DashFactory<>(SoloCreator.create(dashObjects.stream().findFirst().get()), failCallback);
		}
	}

	public D create(R raw, RegistryWriter writer) {
		try {
			return creator.create(raw, writer);
		} catch (Throwable e) {
			try {
				final D failed = failCallback.failed(raw, writer);
				if (failed != null) {
					return failed;
				} else {
					throw new CreationError("Could not find a way to create " + raw.getClass().getSimpleName());
				}
			} catch (Throwable throwable) {
				throw new RuntimeException("Fail in " + this.getClass().getSimpleName(), e);
			}
		}
	}

	@FunctionalInterface
	public interface FailCallback<R, D extends Dashable<R>> {
		D failed(R raw, RegistryWriter writer);
	}
}
