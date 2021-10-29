package dev.quantumfusion.dashloader.core.util;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
public record DashableEntry<D extends Dashable<?>>(int pos, D dashable) {
	public DashableEntry(int pos, Object dashable) {
		this(pos, (D) dashable);
	}
}
