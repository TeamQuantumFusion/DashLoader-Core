package dev.quantumfusion.dashloader.core.thread;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.concurrent.RecursiveAction;
import java.util.function.Function;

public final class IndexedArrayMapTask<I, O> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final IndexedArrayEntry<I>[] inArray;
	private final O[] outArray;
	private final Function<I, O> function;

	private IndexedArrayMapTask(IndexedArrayEntry<I>[] inArray, O[] outArray, Function<I, O> function, int threshold, int start, int stop) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.inArray = inArray;
		this.outArray = outArray;
		this.function = function;
	}

	public IndexedArrayMapTask(IndexedArrayEntry<I>[] inArray, O[] outArray, Function<I, O> function) {
		this.start = 0;
		this.stop = inArray.length;
		this.threshold = ThreadHandler.calcThreshold(stop);
		this.inArray = inArray;
		this.outArray = outArray;
		this.function = function;
	}
	@Override
	protected void compute() {
		final int size = stop - start;
		if (size < threshold) {
			for (int i = start; i < stop; i++) {
				var entry = inArray[i];
				outArray[entry.pos] = function.apply(entry.object);
			}
		} else {
			final int middle = start + (size / 2);
			invokeAll(new IndexedArrayMapTask<>(inArray, outArray, function, threshold, start, middle),
					new IndexedArrayMapTask<>(inArray, outArray, function, threshold, middle, stop));
		}
	}

	@Data
	public record IndexedArrayEntry<O>(O object, int pos) {
	}
}
