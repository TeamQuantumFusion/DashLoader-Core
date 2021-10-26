package net.oskarstrom.dashloader.core.registry.export;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.ThreadManager;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;

import java.util.Arrays;
import java.util.Objects;

@Data
public class MultiStageExportData<F, D extends Dashable<F>> implements ExportData<F, D> {
	// first is stage, then the object
	public final ThreadManager.PosEntry<D>[][] dashables;
	public final byte registryPos;
	public final int dashablesSize;


	public MultiStageExportData(ThreadManager.PosEntry<D>[][] dashables,
			byte registryPos,
			int dashablesSize) {
		this.dashables = dashables;
		this.registryPos = registryPos;
		this.dashablesSize = dashablesSize;
	}


	@Override
	public F[] allocateArray() {
		return (F[]) new Object[dashablesSize];
	}

	@Override
	public void export(F[] array, DashExportHandler exportHandler) {
		if (dashables == null || dashables.length == 0) {
			throw new IllegalStateException("Dashables are not available.");
		}
		for (ThreadManager.PosEntry<D>[] objects : dashables) {
			ThreadManager.parallelToUndash(exportHandler, objects, array);
		}
	}

	@Override
	public byte getPos() {
		return registryPos;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultiStageExportData<?, ?> that = (MultiStageExportData<?, ?>) o;
		return registryPos == that.registryPos && dashablesSize == that.dashablesSize && Arrays.deepEquals(dashables, that.dashables);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(registryPos, dashablesSize);
		result = 31 * result + Arrays.deepHashCode(dashables);
		return result;
	}
}
