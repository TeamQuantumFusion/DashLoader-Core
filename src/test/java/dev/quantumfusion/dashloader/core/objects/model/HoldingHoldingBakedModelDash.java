package dev.quantumfusion.dashloader.core.objects.model;

import dev.quantumfusion.dashloader.core.api.annotation.DashDependencies;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(HoldingHoldingBakedModel.class)
@DashDependencies(HoldingBakedModelDash.class)
public record HoldingHoldingBakedModelDash(int holdingBakedModel) implements ModelDash {

	public HoldingHoldingBakedModelDash(HoldingHoldingBakedModel holdingHoldingBakedModel, DashRegistryWriter writer) {
		this(writer.add(holdingHoldingBakedModel.holdingBakedModel()));
	}

	@Override
	public HoldingHoldingBakedModel export(DashRegistryReader registry) {
		return new HoldingHoldingBakedModel(registry.get(holdingBakedModel));
	}
}
