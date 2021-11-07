package dev.quantumfusion.dashloader.core.client.config;

import com.google.gson.Gson;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public class DashConfigHandler {
	public static final DashConfigHandler INSTANCE = new DashConfigHandler();
	private final Gson gson = new Gson();
	private DashConfig config = new DashConfig();
	private Path configPath;

	@Nullable
	private FileAlterationObserver observer;

	private DashConfigHandler() {
	}

	public void setConfigPath(Path path) {
		if (observer != null) throw new RuntimeException("Tried to set path when its already used");
		this.configPath = path;
	}

	public void reloadConfig() {
		try {
			if (Files.exists(configPath)) {
				this.config = gson.fromJson(Files.newBufferedReader(configPath), DashConfig.class);
				return;
			}
		} catch (Throwable ignored) {}

		// if something fails or the file does not exist
		saveConfig();
	}

	public void saveConfig() {
		try {
			Files.createDirectories(configPath.getParent());
			this.gson.toJson(this.config, Files.newBufferedWriter(configPath, StandardOpenOption.CREATE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addListener(Consumer<DashConfig> configListener) {
		if (observer == null) {
			File directory = configPath.getParent().toFile();
			observer = new FileAlterationObserver(directory);
			FileAlterationMonitor monitor = new FileAlterationMonitor(100);
			monitor.addObserver(observer);
			try {
				monitor.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		observer.addListener(new FileAlterationListener() {
			@Override
			public void onStart(FileAlterationObserver observer) {

			}

			@Override
			public void onDirectoryCreate(File directory) {

			}

			@Override
			public void onDirectoryChange(File directory) {

			}

			@Override
			public void onDirectoryDelete(File directory) {

			}

			@Override
			public void onFileCreate(File file) {

			}

			@Override
			public void onFileChange(File file) {
				try {
					if (Files.isSameFile(Path.of(file.toURI()), configPath)) {
						reloadConfig();
						configListener.accept(config);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFileDelete(File file) {

			}

			@Override
			public void onStop(FileAlterationObserver observer) {

			}
		});
	}
}
