package dev.quantumfusion.dashloader.core.objects.model;

import dev.quantumfusion.dashloader.core.api.annotation.DashDependencies;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(HoldingBakedModel.class)
@DashDependencies(BakedModelDash.class)
public record HoldingBakedModelDash(int basicModel,
									int pos) implements ModelDash {

	public HoldingBakedModelDash(HoldingBakedModel model, DashRegistryWriter writer) {
		this(writer.add(model.basicModel()), model.pos());
	}

	@Override
	public HoldingBakedModel export(DashRegistryReader registry) {
		return new HoldingBakedModel(registry.get(basicModel), pos);
	}
}
