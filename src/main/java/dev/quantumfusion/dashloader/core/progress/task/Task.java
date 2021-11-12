package dev.quantumfusion.dashloader.core.progress.task;

public abstract class Task {
	public abstract double getProgress();

	public abstract Task getCurrentContext();

	public abstract void setSubtask(Task subtask);
}
