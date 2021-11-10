package dev.quantumfusion.dashloader.core.io;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CacheArea {
	public final List<SubCacheArea> subCaches;
	public final String name;

	transient Map<String, SubCacheArea> subCachesMap = new HashMap<>();

	public CacheArea(List<SubCacheArea> subCaches, String name) {
		this.subCaches = subCaches;
		this.name = name;
	}

	public Path getPath(final Path cacheDir, final SubCacheArea subCacheArea) {
		return cacheDir.resolve(name + "/" + subCacheArea.name + "/");
	}

	public void clear(final Path cacheDir) {
		for (SubCacheArea subCache : subCaches) {
			final Path path = getPath(cacheDir, subCache);
			try {
				Files.deleteIfExists(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Files.deleteIfExists(cacheDir.resolve(name + "/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append('\n');

		for (SubCacheArea subCache : subCaches) {
			sb.append("\t").append(subCache.name).append(" | ").append(subCache.used).append('\n');
		}

		return sb.toString();
	}
}
