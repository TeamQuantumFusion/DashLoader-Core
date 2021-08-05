package net.oskarstrom.dashloader.core;

import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadManager {
	private static final int THRESHOLD = 100;
	@Nullable
	private ForkJoinPool dashExecutionPool;

	public void init() {
		dashExecutionPool = new ForkJoinPool(4, new ForkJoinPool.ForkJoinWorkerThreadFactory() {
			private final AtomicInteger threadNumber = new AtomicInteger(1);

			@Override
			public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				final ForkJoinWorkerThread dashThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
				dashThread.setDaemon(true);
				dashThread.setName("dashloaderc-thread-" + threadNumber.getAndIncrement());
				return dashThread;
			}
		}, null, true);
	}

	public <F, D extends Dashable<F>> void parallelToUndash(DashRegistry registry, D[] dashArray, F[] outputArray) {
		ensureReadyForExecution();
		//noinspection ConstantConditions
		dashExecutionPool.invoke(new UndashTask<>(registry, dashArray, outputArray));
	}

	private void ensureReadyForExecution() {
		if (dashExecutionPool == null || dashExecutionPool.isTerminated()) {
			throw new NullPointerException("ThreadPool not initialized");
		}
	}

	public static class UndashTask<D extends Dashable<F>, F> extends RecursiveAction {
		private final int start;
		private final int stop;
		private final D[] startArray;
		private final F[] outputArray;
		private final DashRegistry registry;

		public UndashTask(DashRegistry registry, D[] startArray, F[] outputArray) {
			this.registry = registry;
			this.start = 0;
			this.stop = startArray.length;
			this.startArray = startArray;
			this.outputArray = outputArray;
		}

		private UndashTask(DashRegistry registry, int start, int stop, D[] startArray, F[] outputArray) {
			this.registry = registry;
			this.start = start;
			this.stop = stop;
			this.startArray = startArray;
			this.outputArray = outputArray;
		}

		@Override
		protected void compute() {
			final int size = stop - start;
			if (size <= THRESHOLD) {
				computeTask();
			} else {
				final int middle = start + (size / 2);
				final UndashTask<D, F> alpha = new UndashTask<>(registry, start, middle - 1, startArray, outputArray);
				final UndashTask<D, F> beta = new UndashTask<>(registry, middle, stop, startArray, outputArray);
				invokeAll(alpha, beta);
			}
		}

		private void computeTask() {
			for (int i = start; i <= stop; i++)
				outputArray[i] = startArray[i].toUndash(registry);
		}
	}

}
