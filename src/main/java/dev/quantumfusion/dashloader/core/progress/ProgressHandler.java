package dev.quantumfusion.dashloader.core.progress;

import java.util.concurrent.atomic.AtomicInteger;

public final class ProgressHandler {
	private final AtomicInteger totalTasks = new AtomicInteger(1);
	private final AtomicInteger completeTasks = new AtomicInteger(0);
	private final AtomicInteger totalSubTasks = new AtomicInteger(1);
	private final AtomicInteger completeSubTasks = new AtomicInteger(0);

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
		return this;
	}

	public ProgressHandler completedTask() {
		completeTasks.incrementAndGet();
		return this;
	}

	public ProgressHandler completedSubTask() {
		completeSubTasks.incrementAndGet();
		return this;
	}

	private void tickProgress() {
		final double totalComplete = completeTasks.get() / (double) totalTasks.get();
		final double subComplete = completeSubTasks.get() / (double) totalSubTasks.get();
		final double subMargin = 1 / (double) totalTasks.get();
		this.actualProgress = totalComplete + (subComplete * subMargin);

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
