package dev.quantumfusion.dashloader.core.util.task;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashConstructor;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.ui.DashLoaderProgress;
import dev.quantumfusion.dashloader.core.util.DashThreading;

import java.util.concurrent.RecursiveAction;

public class WriteTask<R, D extends Dashable<R>> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final D[] outArray;
	private final Object[] inArray;
	private final DashConstructor<R, D> constructor;
	private final DashRegistryWriter registry;

	public WriteTask(int threshold, int start, int stop, D[] outArray, Object[] inArray, DashConstructor<R, D> constructor, DashRegistryWriter registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.outArray = outArray;
		this.inArray = inArray;
		this.constructor = constructor;
		this.registry = registry;
	}

	public WriteTask(String name, D[] outArray, Object[] inArray, DashConstructor<R, D> constructor, DashRegistryWriter registry) {
		DashLoaderProgress.PROGRESS.setCurrentSubtask(name, inArray.length);
		this.start = 0;
		this.stop = outArray.length;
		this.threshold = Math.max(this.stop / DashThreading.CORES, 8);
		this.outArray = outArray;
		this.inArray = inArray;
		this.constructor = constructor;
		this.registry = registry;
	}

	@Override
	protected void compute() {
		final int size = stop - start;
		if (size < threshold) computeTask();
		else {
			final int middle = start + (size / 2);
			final WriteTask<R, D> alpha = new WriteTask<>(threshold, start, middle, outArray, inArray, constructor, registry);
			final WriteTask<R, D> beta = new WriteTask<>(threshold, middle, stop, outArray, inArray, constructor, registry);
			alpha.fork();
			beta.fork();
			alpha.join();
			beta.join();
		}
	}

	private void computeTask() {
		for (int i = start; i < stop; i++) {
			outArray[i] = constructor.invoke((R) inArray[i], registry);
			DashLoaderProgress.PROGRESS.completedSubTask();
		}
	}
}
