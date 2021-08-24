package net.oskarstrom.dashloader.api.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistryStorageData<D> {
	@Serialize(order = 0)
	public final List<D> dashables;
	@Serialize(order = 1)
	public final int registryPos;

	public RegistryStorageData(@Deserialize("dashables") List<D> dashables,
							   @Deserialize("registryPos") int registryPos) {
		this.dashables = dashables;
		this.registryPos = registryPos;
	}

	public static <D> RegistryStorageData<D> create(D[] dashables, int registryPos) {
		List<D> out = new ArrayList<>(dashables.length);
		out.addAll(Arrays.asList(dashables));
		return new RegistryStorageData<>(out, registryPos);
	}


	public <DENF extends Dashable<?>> DENF[] getDashables() {
		//noinspection unchecked,ToArrayCallWithZeroLengthArrayArgument
		return (DENF[]) dashables.toArray(new Object[dashables.size()]);
	}
}
