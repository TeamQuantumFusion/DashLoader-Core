package dev.quantumfusion.dashloader.core.io;

import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
public record CacheAreaKey(String cacheName, String subCacheName) {
}
