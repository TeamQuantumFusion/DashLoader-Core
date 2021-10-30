package dev.quantumfusion.dashloader.core.objects.holder;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.annotation.DashDependencies;
import dev.quantumfusion.dashloader.core.api.annotation.DashObject;
import dev.quantumfusion.dashloader.core.objects.model.ModelDash;
import dev.quantumfusion.dashloader.core.registry.DashRegistryReader;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(HoldingHoldingHoldingModel.class)
@DashDependencies(ModelDash.class)
public record HoldingHoldingHoldingModelDash(int model) implements Dashable<HoldingHoldingHoldingModel> {

	public HoldingHoldingHoldingModelDash(HoldingHoldingHoldingModel model, DashRegistryWriter writer) {
		this(writer.add(model.model()));
	}

	@Override
	public HoldingHoldingHoldingModel export(DashRegistryReader reader) {
		return new HoldingHoldingHoldingModel(reader.get(model));
	}
}
