package net.oskarstrom.dashloader.api.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;

@SuppressWarnings("rawtypes") //shh
public class RegistryStorageData<D extends Dashable> {
	@Serialize(order = 0)
	public final D[] dashables;
	@Serialize(order = 1)
	public final int registryPos;

	public RegistryStorageData(@Deserialize("dashables") D[] dashables,
							   @Deserialize("registryPos") int registryPos) {
		this.dashables = dashables;
		this.registryPos = registryPos;
	}
}
