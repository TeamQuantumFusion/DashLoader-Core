package dev.quantumfusion.dashloader.core.util.task;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.ui.DashLoaderProgress;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.concurrent.RecursiveAction;

public class MultiWriteTask<R, D extends Dashable<R>> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final D[] outArray;
	private final Object[] inArray;
	private final Creator<R, D> callback;
	private final Object2ObjectMap<Class<R>, Creator<R, D>> constructors;
	private final DashRegistryWriter registry;

	private MultiWriteTask(int threshold, int start, int stop, D[] outArray, Object[] inArray, Creator<R, D> callback, Object2ObjectMap<Class<R>, Creator<R, D>> constructors, DashRegistryWriter registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.outArray = outArray;
		this.inArray = inArray;
		this.callback = callback;
		this.constructors = constructors;
		this.registry = registry;
	}

	public MultiWriteTask(D[] outArray, Object[] inArray, Creator<R, D> callback, Object2ObjectMap<Class<R>, Creator<R, D>> constructors, DashRegistryWriter registry) {
		this.callback = callback;
		this.start = 0;
		this.stop = outArray.length;
		this.threshold = DashThreading.calcThreshold(stop);
		this.outArray = outArray;
		this.inArray = inArray;
		this.constructors = constructors;
		this.registry = registry;
	}

	@Override
	protected final void compute() {
		final int size = stop - start;
		if (size < threshold) computeTask();
		else {
			final int middle = start + (size / 2);
			invokeAll(new MultiWriteTask<>(threshold, start, middle, outArray, inArray, callback, constructors, registry),
					  new MultiWriteTask<>(threshold, middle, stop, outArray, inArray, callback, constructors, registry));
		}
	}

	private final void computeTask() {
		for (int i = start; i < stop; i++) {
			final R raw = (R) inArray[i];
			final Creator<R, D> rdDashConstructor = constructors.get(raw);
			if (rdDashConstructor == null) {
				outArray[i] = callback.fallback(raw, registry, new NullPointerException("Constructor not found for " + raw.getClass().getSimpleName()));
			} else outArray[i] = rdDashConstructor.create(raw, registry);
			DashLoaderProgress.PROGRESS.completedSubTask();
		}
	}
}
