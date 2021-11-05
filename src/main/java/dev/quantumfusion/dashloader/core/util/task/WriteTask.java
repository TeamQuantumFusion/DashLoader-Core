package dev.quantumfusion.dashloader.core.util.task;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.ui.DashLoaderProgress;
import dev.quantumfusion.dashloader.core.util.DashThreading;

import java.util.concurrent.RecursiveAction;

@SuppressWarnings({"FinalMethodInFinalClass", "FinalPrivateMethod"})
public final class WriteTask<R, D extends Dashable<R>> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final D[] outArray;
	private final Object[] inArray;
	private final Creator<R, D> constructor;
	private final DashRegistryWriter registry;

	private WriteTask(int threshold, int start, int stop, D[] outArray, Object[] inArray, Creator<R, D> constructor, DashRegistryWriter registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.outArray = outArray;
		this.inArray = inArray;
		this.constructor = constructor;
		this.registry = registry;
	}

	public WriteTask(D[] outArray, Object[] inArray, Creator<R, D> constructor, DashRegistryWriter registry) {
		this.start = 0;
		this.stop = outArray.length;
		this.threshold = DashThreading.calcThreshold(stop);
		this.outArray = outArray;
		this.inArray = inArray;
		this.constructor = constructor;
		this.registry = registry;
	}

	@Override
	protected final void compute() {
		final int size = stop - start;
		if (size < threshold) computeTask();
		else {
			final int middle = start + (size / 2);
			invokeAll(new WriteTask<>(threshold, start, middle, outArray, inArray, constructor, registry),
					  new WriteTask<>(threshold, middle, stop, outArray, inArray, constructor, registry));
		}
	}

	private final void computeTask() {
		for (int i = start; i < stop; i++) {
			outArray[i] = constructor.create((R) inArray[i], registry);
			DashLoaderProgress.PROGRESS.completedSubTask();
		}
	}
}
