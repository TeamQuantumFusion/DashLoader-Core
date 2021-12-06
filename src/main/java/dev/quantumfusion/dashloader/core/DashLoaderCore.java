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
public final class DashLoaderCore {
	public static final String PRINT_PREFIX = "[dl-core]: ";
	public static DashLoaderCore CORE;
	public static ConfigHandler CONFIG;
	public static RegistryHandler REGISTRY;
	public static ThreadHandler THREAD;
	public static ProgressHandler PROGRESS;
	public static IOHandler IO;

	private static boolean INITIALIZED = false;
	private static boolean PREPARED = false;
	private static boolean LAUNCHED = false;

	private final Printer print;
	private final Path cacheDir;
	private final Path configPath;

	private DashLoaderCore(Printer print, Path cacheDir, Path configPath) {
		this.print = print;
		this.cacheDir = cacheDir;
		this.configPath = configPath;
		INITIALIZED = true;
	}

	public static void initialize(Path cacheDir, Path configPath, Printer print) {
		if (INITIALIZED) throw new RuntimeException("Core is already initialized");
		CORE = new DashLoaderCore(print, cacheDir, configPath);
	}

	public void prepareCore() {
		if (PREPARED) throw new RuntimeException("Core is already prepared");
		CONFIG = new ConfigHandler("DashLoaderCore property. OwO", configPath);
		THREAD = new ThreadHandler("DashLoaderCore property. UwU");
		PROGRESS = new ProgressHandler("DashLoaderCore property. ^w^");
		PREPARED = true;
	}

	public void launchCore(Collection<Class<?>> dashClasses) {
		if (LAUNCHED) throw new RuntimeException("Core is already launched");
		final var dashObjects = parseDashObjects(dashClasses);
		IO = new IOHandler(dashObjects, "DashLoaderCore property. >w<", cacheDir);
		REGISTRY = new RegistryHandler(dashObjects);
		LAUNCHED = true;

	}

	private static List<DashObjectClass<?, ?>> parseDashObjects(Collection<Class<?>> dashClasses) {
		var out = new ArrayList<DashObjectClass<?, ?>>();
		for (Class<?> dashClass : dashClasses) {
			out.add(new DashObjectClass(dashClass));
		}
		return Collections.unmodifiableList(out);
	}

	// Print things
	public void info(String info) {
		print.info.accept(PRINT_PREFIX + info);
	}

	public void warn(String info) {
		print.warn.accept(PRINT_PREFIX + info);
	}

	public void error(String info) {
		print.error.accept(PRINT_PREFIX + info);
	}

	public static final class Printer {
		private final Consumer<String> info;
		private final Consumer<String> warn;
		private final Consumer<String> error;

		public Printer(Consumer<String> info, Consumer<String> warn, Consumer<String> error) {
			this.info = info;
			this.warn = warn;
			this.error = error;
		}
	}
}
