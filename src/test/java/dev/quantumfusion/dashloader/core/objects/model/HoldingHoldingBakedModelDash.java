package dev.quantumfusion.dashloader.core.objects.model;

import dev.quantumfusion.dashloader.core.api.DashDependencies;
import dev.quantumfusion.dashloader.core.api.DashObject;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(HoldingHoldingBakedModel.class)
@DashDependencies(HoldingBakedModelDash.class)
public record HoldingHoldingBakedModelDash(int holdingBakedModel) implements ModelDash {

	public HoldingHoldingBakedModelDash(HoldingHoldingBakedModel holdingHoldingBakedModel, RegistryWriter writer) {
		this(writer.add(holdingHoldingBakedModel.holdingBakedModel()));
	}

	@Override
	public HoldingHoldingBakedModel export(RegistryReader registry) {
		return new HoldingHoldingBakedModel(registry.get(holdingBakedModel));
	}
}
