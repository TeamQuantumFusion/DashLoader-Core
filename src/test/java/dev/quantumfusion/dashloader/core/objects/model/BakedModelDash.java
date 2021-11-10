package dev.quantumfusion.dashloader.core.objects.model;

import dev.quantumfusion.dashloader.core.api.DashDependencies;
import dev.quantumfusion.dashloader.core.api.DashObject;
import dev.quantumfusion.dashloader.core.objects.IdentifierDash;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(BakedModel.class)
@DashDependencies(IdentifierDash.class)
public record BakedModelDash(int image, int identifier,
							 String anotherThing) implements ModelDash {

	public BakedModelDash(BakedModel model, RegistryWriter writer) {
		this(model.image(), writer.add(model.identifier()), model.anotherThing());
	}

	@Override
	public BakedModel export(RegistryReader registry) {
		return new BakedModel(image, registry.get(identifier), anotherThing);
	}
}
