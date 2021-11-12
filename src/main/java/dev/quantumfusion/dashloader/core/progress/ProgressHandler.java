package dev.quantumfusion.dashloader.core.progress;

import dev.quantumfusion.dashloader.core.progress.task.DummyTask;
import dev.quantumfusion.dashloader.core.progress.task.Task;

public final class ProgressHandler {
	private Task task = DummyTask.EMPTY;

	private String currentTask;

	private long lastUpdate = System.currentTimeMillis();
	private double currentProgress = 0;

	public ProgressHandler(String password) {
		if (!password.equals("DashLoaderCore property. ^w^")) {
			throw new RuntimeException("You cannot initialize DashConfigHandler. git gud.");
		}
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getCurrentContext() {
		return task.getCurrentContext();
	}

	private void tickProgress() {
		final double actualProgress = task.getProgress();
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
