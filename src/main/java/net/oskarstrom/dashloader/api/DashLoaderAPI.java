package net.oskarstrom.dashloader.api;

import net.oskarstrom.dashloader.api.registry.RegistryStorage;

import java.util.List;
import java.util.function.Consumer;

public interface DashLoaderAPI {
	<F> void registerRegistry(List<Class<F>> mappings, RegistryStorage<F> storage, Consumer<RegistryStorage<?>> consumer);


}
