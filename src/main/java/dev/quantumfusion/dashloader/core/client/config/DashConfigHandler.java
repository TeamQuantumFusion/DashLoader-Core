package dev.quantumfusion.dashloader.core.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public class DashConfigHandler {
	public static final DashConfigHandler INSTANCE = new DashConfigHandler();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private Path configPath;
	public DashConfig config = new DashConfig();

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
				final BufferedReader json = Files.newBufferedReader(configPath);
				this.config = gson.fromJson(json, DashConfig.class);
				json.close();
				return;
			}
		} catch (Throwable ignored) {}

		// if something fails or the file does not exist
		saveConfig();
	}

	public void saveConfig() {
		try {
			Files.createDirectories(configPath.getParent());
			final BufferedWriter writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE);
			this.gson.toJson(this.config, writer);
			writer.close();
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
