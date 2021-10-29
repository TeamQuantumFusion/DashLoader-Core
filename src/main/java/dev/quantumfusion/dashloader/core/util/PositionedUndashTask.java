package dev.quantumfusion.dashloader.core.util;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;

import java.util.concurrent.RecursiveAction;

public class PositionedUndashTask<R, D extends Dashable<R>> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final DashableEntry<D>[] dashArray;
	private final Object[] outArray;
	private final DashRegistryReader registry;

	public PositionedUndashTask(int threshold, int start, int stop, DashableEntry<D>[] dashArray, Object[] outArray, DashRegistryReader registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.dashArray = dashArray;
		this.outArray = outArray;
		this.registry = registry;
	}

	public PositionedUndashTask(DashableEntry<D>[] dashArray, Object[] outArray, DashRegistryReader registry) {
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
			final PositionedUndashTask<R, D> alpha = new PositionedUndashTask<>(threshold, start, middle, dashArray, outArray, registry);
			final PositionedUndashTask<R, D> beta = new PositionedUndashTask<>(threshold, middle, stop, dashArray, outArray, registry);
			invokeAll(alpha, beta);
		}
	}

	private void computeTask() {
		for (int i = start; i < stop; i++) {
			final DashableEntry<D> entry = dashArray[i];
			outArray[entry.pos()] = entry.dashable().export(registry);
		}
	}
}
