package dev.quantumfusion.dashloader.core.progress;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class ProgressHandler {
	private final AtomicInteger totalTasks = new AtomicInteger(1);
	private final AtomicInteger completeTasks = new AtomicInteger(0);

	private final AtomicInteger totalSubTasks = new AtomicInteger(1);
	private final AtomicInteger completeSubTasks = new AtomicInteger(0);

	@Nullable
	private Supplier<Double> subSubTaskProgress;

	private String currentTask;

	private long lastUpdate = System.currentTimeMillis();
	private double currentProgress = 0;
	private double actualProgress = 0;

	public ProgressHandler(String password) {
		if (!password.equals("DashLoaderCore property. ^w^")) {
			throw new RuntimeException("You cannot initialize DashConfigHandler. git gud.");
		}
	}

	public ProgressHandler startTask(int tasks) {
		this.totalTasks.set(tasks);
		this.completeSubTasks.set(0);

		this.currentProgress = 0;
		this.actualProgress = 0;
		return this;
	}

	public ProgressHandler startSubTask(int tasks) {
		this.totalSubTasks.set(tasks);
		this.completeSubTasks.set(0);
		this.subSubTaskProgress = null;
		return this;
	}

	public void task(Runnable runnable) {
		runnable.run();
		completedTask();
	}

	public void subTask(Runnable runnable) {
		runnable.run();
		completedSubTask();
	}

	public ProgressHandler completedTask() {
		completeTasks.incrementAndGet();
		return this;
	}

	public ProgressHandler completedSubTask() {
		completeSubTasks.incrementAndGet();
		this.subSubTaskProgress = null;
		return this;
	}

	public ProgressHandler setSubSubtaskProgressProvider(Supplier<Double> subSubtaskProgressProvider) {
		subSubTaskProgress = subSubtaskProgressProvider;
		return this;
	}

	private void tickProgress() {
		final double totalComplete = completeTasks.get() / (double) totalTasks.get();
		final double subComplete = completeSubTasks.get() / (double) totalSubTasks.get();
		final double subSubComplete = subSubTaskProgress == null ? 0 : subSubTaskProgress.get();

		final double subMargin = 1 / (double) totalTasks.get();
		final double subSubMargin = 1 / (double) totalSubTasks.get();
		this.actualProgress = totalComplete + ((subComplete + (subSubComplete * subSubMargin)) * subMargin);

		final double divisionSpeed = (actualProgress < currentProgress) ? 3 : 10;
		this.currentProgress += (actualProgress - currentProgress) / divisionSpeed;
	}

	public double getProgress() {
		final long currentTime = System.currentTimeMillis();
		while (currentTime > lastUpdate) {
			tickProgress();
			lastUpdate += 10; // ~100ups
		}
		return currentProgress;
	}

	public String getCurrentTask() {
		return currentTask;
	}

	public ProgressHandler setCurrentTask(String currentTask) {
		this.currentTask = currentTask;
		return this;
	}


}
