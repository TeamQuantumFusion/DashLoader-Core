package dev.quantumfusion.dashloader.core.util;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class DashThreading {
	private static final int CORES = Runtime.getRuntime().availableProcessors();
	private static ForkJoinPool THREAD_POOL;

	public static void init() {
		THREAD_POOL = new ForkJoinPool(CORES, new ForkJoinPool.ForkJoinWorkerThreadFactory() {
			private final AtomicInteger threadNumber = new AtomicInteger(1);

			@Override
			public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				final ForkJoinWorkerThread dashThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
				dashThread.setDaemon(true);
				dashThread.setName("dashloaderc-thread-" + threadNumber.getAndIncrement());
				return dashThread;
			}
		}, (t, e) -> {
			System.out.println(t.getName() + " failed, ERROR: " + e.getMessage());
			e.printStackTrace();
		}, true);
	}

	private static void ensurePoolAlive() {
		if (THREAD_POOL == null || THREAD_POOL.isTerminated())
			throw new NullPointerException("ThreadPool not initialized");
	}

	public static <R, D extends Dashable<R>> void export(D[] dashables, Object[] data, DashRegistryReader registry) {
		ensurePoolAlive();
		THREAD_POOL.invoke(new UndashTask<>(dashables, data, registry));
	}

	public static <R, D extends Dashable<R>> void export(DashableEntry<D>[] dashables, Object[] data, DashRegistryReader registry) {
		ensurePoolAlive();
		THREAD_POOL.invoke(new PositionedUndashTask<>(dashables, data, registry));
	}

	public static class UndashTask<R, D extends Dashable<R>> extends RecursiveAction {
		private final int threshold;
		private final int start;
		private final int stop;
		private final D[] dashArray;
		private final Object[] rawArray;
		private final DashRegistryReader registry;

		public UndashTask(int threshold, int start, int stop, D[] dashArray, Object[] rawArray, DashRegistryReader registry) {
			this.threshold = threshold;
			this.start = start;
			this.stop = stop;
			this.dashArray = dashArray;
			this.rawArray = rawArray;
			this.registry = registry;
		}

		public UndashTask(D[] dashArray, Object[] rawArray, DashRegistryReader registry) {
			this.threshold = dashArray.length / CORES;
			this.start = 0;
			this.stop = dashArray.length;
			this.dashArray = dashArray;
			this.rawArray = rawArray;
			this.registry = registry;
		}

		@Override
		protected void compute() {
			final int size = stop - start;
			if (size < threshold) computeTask();
			else {
				final int middle = start + (size / 2);
				final UndashTask<R, D> alpha = new UndashTask<>(threshold, start, middle, dashArray, rawArray, registry);
				final UndashTask<R, D> beta = new UndashTask<>(threshold, middle, stop, dashArray, rawArray, registry);
				invokeAll(alpha, beta);
			}
		}

		private void computeTask() {
			for (int i = start; i < stop; i++)
				rawArray[i] = dashArray[i].export(registry);
		}
	}

	public static class PositionedUndashTask<R, D extends Dashable<R>> extends RecursiveAction {
		private final int threshold;
		private final int start;
		private final int stop;
		private final DashableEntry<D>[] dashArray;
		private final Object[] rawArray;
		private final DashRegistryReader registry;

		public PositionedUndashTask(int threshold, int start, int stop, DashableEntry<D>[] dashArray, Object[] rawArray, DashRegistryReader registry) {
			this.threshold = threshold;
			this.start = start;
			this.stop = stop;
			this.dashArray = dashArray;
			this.rawArray = rawArray;
			this.registry = registry;
		}

		public PositionedUndashTask(DashableEntry<D>[] dashArray, Object[] rawArray, DashRegistryReader registry) {
			this.threshold = dashArray.length / CORES;
			this.start = 0;
			this.stop = dashArray.length;
			this.dashArray = dashArray;
			this.rawArray = rawArray;
			this.registry = registry;
		}

		@Override
		protected void compute() {
			final int size = stop - start;
			if (size < threshold) computeTask();
			else {
				final int middle = start + (size / 2);
				final PositionedUndashTask<R, D> alpha = new PositionedUndashTask<>(threshold, start, middle, dashArray, rawArray, registry);
				final PositionedUndashTask<R, D> beta = new PositionedUndashTask<>(threshold, middle, stop, dashArray, rawArray, registry);
				invokeAll(alpha, beta);
			}
		}

		private void computeTask() {
			for (int i = start; i < stop; i++) {
				final DashableEntry<D> entry = dashArray[i];
				rawArray[entry.pos] = entry.dashable.export(registry);
			}
		}
	}

	@Data
	public record DashableEntry<D extends Dashable<?>>(int pos, D dashable) {

	}


}
