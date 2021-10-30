package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;

public interface Dashable<R> {
	/**
	 * Runs before toUndash on a single thread
	 */
	default void prepare(DashRegistryReader writer) {
	}

	/**
	 * Runs after toUndash on a single thread
	 */
	default void apply(DashRegistryReader reader) {
	}

	/**
	 * Runs in parallel returning the target object.
	 */
	R export(DashRegistryReader registry);
}
