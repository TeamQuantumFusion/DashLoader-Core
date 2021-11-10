package dev.quantumfusion.dashloader.core;

import dev.quantumfusion.dashloader.core.config.ConfigHandler;
import dev.quantumfusion.dashloader.core.io.IOHandler;
import dev.quantumfusion.dashloader.core.progress.ProgressHandler;
import dev.quantumfusion.dashloader.core.registry.RegistryHandler;
import dev.quantumfusion.dashloader.core.thread.ThreadHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * The heart of dashloader, Handles Config, IO and the serialization
 */
public final class DashLoaderCore<R, D extends Dashable<R>> {
	public static DashLoaderCore<?, ?> CORE;
	// Basic Information
	private final List<DashObjectClass<R, D>> dashObjects;
	private final Consumer<String> print;
	private final Path cacheDir;
	private final Path configDir;

	// Handlers
	private final RegistryHandler<R, D> registryHandler;
	private final ThreadHandler threadHandler;
	private final ConfigHandler configHandler;
	private final ProgressHandler progressHandler;
	private final IOHandler<?> ioHandler;

	public DashLoaderCore(List<DashObjectClass<R, D>> dashObjects, Consumer<String> print, Path cacheDir, Path configDir) {
		this.print = print;
		this.dashObjects = dashObjects;
		this.cacheDir = cacheDir;
		this.configDir = configDir;

		// Handlers
		this.registryHandler = new RegistryHandler<>(dashObjects);
		this.threadHandler = new ThreadHandler("DashLoaderCore property. UwU");
		this.configHandler = new ConfigHandler("DashLoaderCore property. OwO");
		this.progressHandler = new ProgressHandler("DashLoaderCore property. ^w^");
		this.ioHandler = new IOHandler(dashObjects, "DashLoaderCore property. >w<", cacheDir);
	}

	public static <R, D extends Dashable<R>> void initialize(Path cacheDir, Path configDir, Collection<Class<D>> dashClasses, Consumer<String> print) {
		if (CORE != null) throw new RuntimeException("Core is already initialized");
		CORE = new DashLoaderCore<>(parseDashObjects(dashClasses), print, cacheDir, configDir);
	}

	private static <R, D extends Dashable<R>> List<DashObjectClass<R, D>> parseDashObjects(Collection<Class<D>> dashClasses) {
		var out = new ArrayList<DashObjectClass<R, D>>();
		for (Class<D> dashClass : dashClasses) {
			out.add(new DashObjectClass<>(dashClass));
		}
		return Collections.unmodifiableList(out);
	}

	// Getters
	public RegistryHandler<R, D> getRegistryHandler() {
		return registryHandler;
	}

	public ThreadHandler getThreadHandler() {
		return threadHandler;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public ProgressHandler getProgressHandler() {
		return progressHandler;
	}

	public IOHandler<?> getIoHandler() {
		return ioHandler;
	}

	// Print things
	public void info(String info) {
		print.accept("/info/ " + info);
	}

	public void warn(String info) {
		print.accept("/warn/ " + info);
	}

	public void error(String info) {
		print.accept("/error/ " + info);
	}
}
