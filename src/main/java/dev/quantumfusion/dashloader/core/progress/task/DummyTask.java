package dev.quantumfusion.dashloader.core.progress.task;

public class DummyTask extends Task {
	public static final DummyTask EMPTY = new DummyTask(0);
	public static final DummyTask FULL = new DummyTask(1);
	private final double progress;

	private DummyTask(double progress) {
		this.progress = progress;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public Task getCurrentContext() {
		return EMPTY;
	}

	@Override
	public void setSubtask(Task subtask) {
		throw new RuntimeException("Subtasks unsupported in " + this.getClass().getSimpleName());
	}
}
