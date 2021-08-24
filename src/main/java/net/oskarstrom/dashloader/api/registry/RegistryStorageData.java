package net.oskarstrom.dashloader.api.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;

public class RegistryStorageData<D> {
	@Serialize(order = 0)
	public final D[] dashables;
	@Serialize(order = 1)
	public final int registryPos;

	public RegistryStorageData(@Deserialize("dashables") D[] dashables,
							   @Deserialize("registryPos") int registryPos) {
		this.dashables = dashables;
		this.registryPos = registryPos;
	}


	public <DENF extends Dashable<?>> DENF[] getDashables() {
		return (DENF[]) dashables;
	}
}
