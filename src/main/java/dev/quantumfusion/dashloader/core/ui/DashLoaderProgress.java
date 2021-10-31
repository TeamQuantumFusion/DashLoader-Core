package dev.quantumfusion.dashloader.core.ui;

import java.util.concurrent.atomic.AtomicInteger;

public class DashLoaderProgress {
	public static final DashLoaderProgress PROGRESS = new DashLoaderProgress();
	private final AtomicInteger totalTasks = new AtomicInteger(1);
	private final AtomicInteger completedTasks = new AtomicInteger(0);
	private final AtomicInteger totalSubtasks = new AtomicInteger(1);
	private final AtomicInteger completedSubtasks = new AtomicInteger(0);
	private String subtaskName;

	private DashLoaderProgress() {
	}

	public void reset() {
		this.totalTasks.set(1);
		this.completedTasks.set(0);
		this.totalSubtasks.set(1);
		this.completedSubtasks.set(0);
	}

	public void completedSubTask() {
		this.completedSubtasks.incrementAndGet();
	}

	public void completedTask() {
		this.completedTasks.incrementAndGet();
	}

	public void setCurrentSubtask(String taskName, int tasks) {
		this.subtaskName = taskName;
		this.totalSubtasks.set(tasks);
		this.completedSubtasks.set(0);
	}

	public String getSubtaskName() {
		return this.subtaskName;
	}

	public int getTotalTasks() {
		return this.totalTasks.get();
	}

	public void setTotalTasks(int tasks) {
		this.totalTasks.set(tasks);
	}

	public int getCompletedTasks() {
		return this.completedTasks.get();
	}

	public int getTotalSubtasks() {
		return this.totalSubtasks.get();
	}

	public int getCompletedSubtasks() {
		return this.completedSubtasks.get();
	}

	public double getProgress() {
		return (this.completedTasks.get() / (double) this.totalTasks.get()) + (getSubProgress() / totalTasks.get());
	}

	public double getSubProgress() {
		return this.completedSubtasks.get() / (double) this.totalSubtasks.get();
	}

}
