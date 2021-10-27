package net.oskarstrom.dashloader.core.registry.regdata;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.Dashable;
import net.oskarstrom.dashloader.core.ThreadManager;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;

import java.util.Arrays;
import java.util.Objects;

@Data
public class MultiRegistryDataImpl<F, D extends Dashable<F>> implements RegistryData<F, D> {
	public final D[] dashables;
	public final byte registryPos;


	public MultiRegistryDataImpl(D[] dashables,
			byte registryPos) {
		this.dashables = dashables;
		this.registryPos = registryPos;
	}


	@Override
	public F[] allocateArray() {
		return (F[]) new Object[dashables.length];
	}

	@Override
	public void export(F[] array, DashExportHandler exportHandler) {
		if (dashables == null || dashables.length == 0) {
			throw new IllegalStateException("Dashables are not available.");
		}
		ThreadManager.parallelToUndash(exportHandler, dashables, array);
	}

	@Override
	public byte getPos() {
		return registryPos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultiRegistryDataImpl<?, ?> that = (MultiRegistryDataImpl<?, ?>) o;
		return registryPos == that.registryPos && Arrays.equals(dashables, that.dashables);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(registryPos);
		result = 31 * result + Arrays.hashCode(dashables);
		return result;
	}
}
