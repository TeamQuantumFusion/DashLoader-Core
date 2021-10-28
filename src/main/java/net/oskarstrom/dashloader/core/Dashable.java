package net.oskarstrom.dashloader.core;

public interface Dashable<R> {
	/**
	 * Runs before toUndash on a single thread
	 */
	default void prepare() {
	}

	/**
	 * Runs after toUndash on a single thread
	 */
	default void apply() {
	}

	/**
	 * Runs in parallel returning the target object.
	 */
	R toUndash();
}
