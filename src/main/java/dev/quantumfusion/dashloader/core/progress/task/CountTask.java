package dev.quantumfusion.dashloader.core.progress.task;

import java.util.concurrent.atomic.AtomicInteger;

public class CountTask extends Task {
	// double to have fewer casts
	private final double totalTasks;
	private final AtomicInteger completeTasks = new AtomicInteger(0);

	// double to have fewer casts
	private Task currentSubtask = DummyTask.EMPTY;


	public CountTask(int totalTasks) {
		this.totalTasks = totalTasks;
	}

	public void completedTask() {
		this.completeTasks.getAndIncrement();
		this.currentSubtask = DummyTask.EMPTY;
	}


	@Override
	public void setSubtask(Task subTask) {
		this.currentSubtask = subTask;
	}

	@Override
	public double getProgress() {
		return (completeTasks.get() / totalTasks) + (currentSubtask.getProgress() * (1 / totalTasks));
	}

	@Override
	public Task getCurrentContext() {
		if (currentSubtask == DummyTask.EMPTY) return this;
		return currentSubtask;
	}
}
