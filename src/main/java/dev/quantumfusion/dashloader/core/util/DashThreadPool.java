package dev.quantumfusion.dashloader.core.util;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

class DashThreadPool extends ForkJoinPool {
	public DashThreadPool() {
		super(DashThreading.CORES, new ForkJoinPool.ForkJoinWorkerThreadFactory() {
			private final AtomicInteger threadNumber = new AtomicInteger(0);

			@Override
			public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				final ForkJoinWorkerThread dashThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
				dashThread.setDaemon(true);
				dashThread.setName("dlc-thread-" + threadNumber.getAndIncrement());
				return dashThread;
			}
		}, null, true);
	}
}
