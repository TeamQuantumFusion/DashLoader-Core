package dev.quantumfusion.dashloader.core.objects.model;

import dev.quantumfusion.dashloader.core.api.annotation.DashDependencies;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.objects.IdentifierDash;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(BakedModel.class)
@DashDependencies(IdentifierDash.class)
public record BakedModelDash(int image, int identifier,
							 String anotherThing) implements ModelDash {

	public BakedModelDash(BakedModel model, DashRegistryWriter writer) {
		this(model.image(), writer.add(model.identifier()), model.anotherThing());
	}

	@Override
	public BakedModel export(DashRegistryReader registry) {
		return new BakedModel(image, registry.get(identifier), anotherThing);
	}
}
