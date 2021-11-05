package dev.quantumfusion.dashloader.core.util;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.Creator;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.dashloader.core.ui.DashLoaderProgress;
import dev.quantumfusion.dashloader.core.util.task.ExportTask;
import dev.quantumfusion.dashloader.core.util.task.MultiWriteTask;
import dev.quantumfusion.dashloader.core.util.task.PositionedExportTask;
import dev.quantumfusion.dashloader.core.util.task.WriteTask;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class DashThreading {
	public static final int CORES = Runtime.getRuntime().availableProcessors();
	private static ForkJoinPool THREAD_POOL;

	public static void init() {
		THREAD_POOL = new DashThreadPool();
	}

	private static void ensurePoolAlive() {
		if (THREAD_POOL == null || THREAD_POOL.isTerminated())
			throw new NullPointerException("ThreadPool not initialized");
	}

	private static Collection<Callable<Object>> convert(Collection<Runnable> runnables) {
		return runnables.stream().map(Executors::callable).toList();
	}

	public static void run(Runnable... runnables) {
		run(List.of(runnables));
	}

	public static void run(Collection<Runnable> runnables) {
		final List<Future<Object>> futures = THREAD_POOL.invokeAll(convert(runnables));
		for (Future<Object> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public static <R, D extends Dashable<R>> void runExport(D[] dashables, Object[] data, DashRegistryReader registry) {
		ensurePoolAlive();
		THREAD_POOL.invoke(new ExportTask<>(dashables, data, registry));
	}

	public static <R, D extends Dashable<R>> D[] runFloatingSingleWriteTask(String name, List<R> rawList, Creator<R, D> constructor, DashRegistryWriter registry) {
		ensurePoolAlive();
		final Object[] objects = rawList.toArray();
		final D[] dashOut = (D[]) new Dashable[objects.length];
		DashLoaderProgress.PROGRESS.setCurrentSubtask(name, objects.length);
		THREAD_POOL.invoke(new WriteTask<>(dashOut, objects, constructor, registry));
		return dashOut;
	}

	public static <R, D extends Dashable<R>> D[] runFloatingMultiWriteTask(String name, List<R> rawList, Creator<R, D> callback, Object2ObjectMap<Class<R>, Creator<R, D>> constructors, DashRegistryWriter registry) {
		ensurePoolAlive();
		final Object[] objects = rawList.toArray();
		final D[] dashOut = (D[]) new Dashable[objects.length];
		DashLoaderProgress.PROGRESS.setCurrentSubtask(name, objects.length);
		THREAD_POOL.invoke(new MultiWriteTask<>(dashOut, objects, callback, constructors, registry));
		return dashOut;
	}

	public static <R, D extends Dashable<R>> D[] runWriteTask(String name, List<D> dashList) {
		DashLoaderProgress.PROGRESS.setCurrentSubtask(name, 1);
		final D[] objects = (D[]) dashList.toArray(Dashable[]::new);
		DashLoaderProgress.PROGRESS.completedSubTask();
		return objects;
	}

	public static <R, D extends Dashable<R>> void runExport(DashableEntry<D>[] dashables, Object[] data, DashRegistryReader registry) {
		ensurePoolAlive();
		THREAD_POOL.invoke(new PositionedExportTask<>(dashables, data, registry));
	}

	public static int calcThreshold(final int tasks) {
		return Math.max(tasks / (DashThreading.CORES * 32), 4);
	}


}
