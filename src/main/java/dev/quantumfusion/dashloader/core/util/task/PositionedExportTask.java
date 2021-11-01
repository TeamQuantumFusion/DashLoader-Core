package dev.quantumfusion.dashloader.core.util.task;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.util.DashThreading;
import dev.quantumfusion.dashloader.core.util.DashableEntry;

import java.util.concurrent.ForkJoinTask;

@SuppressWarnings({"FinalMethodInFinalClass"})
public final class PositionedExportTask<R, D extends Dashable<R>> extends ForkJoinTask<Void> {
	private final int threshold;
	private final int start;
	private final int stop;
	private final DashableEntry<D>[] dashArray;
	private final Object[] outArray;
	private final DashRegistryReader registry;

	private PositionedExportTask(int threshold, int start, int stop, DashableEntry<D>[] dashArray, Object[] outArray, DashRegistryReader registry) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.dashArray = dashArray;
		this.outArray = outArray;
		this.registry = registry;
	}

	public PositionedExportTask(DashableEntry<D>[] dashArray, Object[] outArray, DashRegistryReader registry) {
		this.start = 0;
		this.stop = dashArray.length;
		this.threshold = DashThreading.calcThreshold(stop);
		this.dashArray = dashArray;
		this.outArray = outArray;
		this.registry = registry;
	}

	@Override
	protected final boolean exec() {
		final int size = stop - start;
		if (size < threshold)
			for (int i = start; i < stop; i++) {
				final var entry = dashArray[i];
				outArray[entry.pos()] = entry.dashable().export(registry);
			}
		else {
			final int middle = start + (size / 2);
			invokeAll(new PositionedExportTask<>(threshold, start, middle, dashArray, outArray, registry),
					  new PositionedExportTask<>(threshold, middle, stop, dashArray, outArray, registry));
		}
		return true;
	}

	public final Void getRawResult() {
		return null;
	}

	protected final void setRawResult(Void mustBeNull) {
	}
}
