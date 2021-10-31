package dev.quantumfusion.dashloader.core.util.task;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.util.DashThreading;

import java.util.concurrent.RecursiveAction;

public class ExportTask<R, D extends Dashable<R>> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final D[] dashArray;
	private final Object[] outArray;
	private final DashRegistryReader registry;

	public ExportTask(int threshold, int start, int stop, D[] dashArray, Object[] outArray, DashRegistryReader registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.dashArray = dashArray;
		this.outArray = outArray;
		this.registry = registry;
	}

	public ExportTask(D[] dashArray, Object[] outArray, DashRegistryReader registry) {
		this.start = 0;
		this.stop = dashArray.length;
		this.threshold = Math.max(this.stop / DashThreading.CORES, 8);
		this.dashArray = dashArray;
		this.outArray = outArray;
		this.registry = registry;
	}

	@Override
	protected void compute() {
		final int size = stop - start;
		if (size < threshold) computeTask();
		else {
			final int middle = start + (size / 2);
			final ExportTask<R, D> alpha = new ExportTask<>(threshold, start, middle, dashArray, outArray, registry);
			final ExportTask<R, D> beta = new ExportTask<>(threshold, middle, stop, dashArray, outArray, registry);
			alpha.fork();
			beta.fork();
			alpha.join();
			beta.join();
		}
	}

	private void computeTask() {
		for (int i = start; i < stop; i++)
			outArray[i] = dashArray[i].export(registry);
	}
}
