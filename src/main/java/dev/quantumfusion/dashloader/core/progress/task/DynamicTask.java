package dev.quantumfusion.dashloader.core.progress.task;

import java.util.function.Supplier;

public class DynamicTask extends Task {
	private final Supplier<Double> supplier;

	public DynamicTask(Supplier<Double> supplier) {
		this.supplier = supplier;
	}

	@Override
	public double getProgress() {
		return supplier.get();
	}

	@Override
	public Task getCurrentContext() {
		return DummyTask.EMPTY;
	}

	@Override
	public void setSubtask(Task subtask) {
		throw new RuntimeException("Subtasks unsupported in " + this.getClass().getSimpleName());
	}
}
