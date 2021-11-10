package dev.quantumfusion.dashloader.core.objects.model;

import dev.quantumfusion.dashloader.core.api.DashDependencies;
import dev.quantumfusion.dashloader.core.api.DashObject;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(HoldingBakedModel.class)
@DashDependencies(BakedModelDash.class)
public record HoldingBakedModelDash(int basicModel,
									int pos) implements ModelDash {

	public HoldingBakedModelDash(HoldingBakedModel model, RegistryWriter writer) {
		this(writer.add(model.basicModel()), model.pos());
	}

	@Override
	public HoldingBakedModel export(RegistryReader registry) {
		return new HoldingBakedModel(registry.get(basicModel), pos);
	}
}
