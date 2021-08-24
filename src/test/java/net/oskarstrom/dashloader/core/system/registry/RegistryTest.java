package net.oskarstrom.dashloader.core.system.registry;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.*;
import net.oskarstrom.dashloader.core.registry.DashRegistryImpl;
import net.oskarstrom.dashloader.core.registry.FactoryConstructorImpl;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistryTest {

	@Test
	@Order(0)
	public void createFactoryConstructor() throws IllegalAccessException, NoSuchMethodException {
		final FactoryConstructor<Integer, DashInteger> constructor = FactoryConstructorImpl.createConstructor(Integer.class, DashInteger.class);
		final DashInteger dashInteger = constructor.create(69, new DashRegistryImpl());
		assertEquals(69, dashInteger.toUndash(null));
	}

	@Test
	@Order(1)
	public void createFullRegistryTest() throws IllegalAccessException, NoSuchMethodException {
		DashRegistry registry = new DashRegistryImpl();
		final RegistryStorage<Integer> storage = RegistryStorageFactory.createSimpleRegistry(registry, Integer.class, DashInteger.class);
		registry.addMapping(Integer.class, registry.addStorage(storage));

		final Integer object = new Integer(420);
		final Pointer add = registry.add(object);

	}

	public static class DashInteger implements Dashable<Integer> {
		@Serialize(order = 0)
		public Boolean integer;

		public DashInteger(@Deserialize("integer") Boolean integer) {
			this.integer = integer;
		}

		public DashInteger(Integer integer, DashRegistry registry) {
			this(integer == 32);
		}

		@Override
		public Integer toUndash(DashRegistry registry) {
			return 69;
		}
	}
}
