package dev.quantumfusion.dashloader.core.util;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;

import java.util.concurrent.RecursiveAction;

public class UndashTask<R, D extends Dashable<R>> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final D[] dashArray;
	private final Object[] outArray;
	private final DashRegistryReader registry;

	public UndashTask(int threshold, int start, int stop, D[] dashArray, Object[] outArray, DashRegistryReader registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.dashArray = dashArray;
		this.outArray = outArray;
		this.registry = registry;
	}

	public UndashTask(D[] dashArray, Object[] outArray, DashRegistryReader registry) {
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
			final UndashTask<R, D> alpha = new UndashTask<>(threshold, start, middle, dashArray, outArray, registry);
			final UndashTask<R, D> beta = new UndashTask<>(threshold, middle, stop, dashArray, outArray, registry);
			invokeAll(alpha, beta);
		}
	}

	private void computeTask() {
		for (int i = start; i < stop; i++)
			outArray[i] = dashArray[i].export(registry);
	}
}
