package net.oskarstrom.dashloader.core.system.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashExportHandler;

public class RegistryTest {

	public static class DashInteger implements Dashable<Integer> {
		@Serialize(order = 0)
		public Boolean integer;

		public DashInteger(@Deserialize("integer") Boolean integer) {
			this.integer = integer;
		}

		public DashInteger(Integer integer, DashExportHandler registry) {
			this(integer == 32);
		}

		@Override
		public Integer toUndash(DashExportHandler registry) {
			return 69;
		}
	}
}
