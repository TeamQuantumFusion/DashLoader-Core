package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.registry.RegistryReader;

public interface Dashable<R> {
	/**
	 * Runs before {@link Dashable#export(RegistryReader)} on a single thread
	 */
	default void preExport(RegistryReader reader) {
	}

	/**
	 * Runs in parallel returning the target object.
	 */
	R export(RegistryReader reader);

	/**
	 * Runs after {@link Dashable#export(RegistryReader)} on a single thread
	 */
	default void postExport(RegistryReader reader) {
	}
}
