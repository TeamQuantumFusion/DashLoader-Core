package dev.quantumfusion.dashloader.core.objects.holder;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.api.DashDependencies;
import dev.quantumfusion.dashloader.core.api.DashObject;
import dev.quantumfusion.dashloader.core.objects.model.ModelDash;
import dev.quantumfusion.dashloader.core.registry.RegistryReader;
import dev.quantumfusion.dashloader.core.registry.RegistryWriter;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
@DashObject(HoldingHoldingHoldingModel.class)
@DashDependencies(ModelDash.class)
public record HoldingHoldingHoldingModelDash(int model) implements Dashable<HoldingHoldingHoldingModel> {

	public HoldingHoldingHoldingModelDash(HoldingHoldingHoldingModel model, RegistryWriter writer) {
		this(writer.add(model.model()));
	}

	@Override
	public HoldingHoldingHoldingModel export(RegistryReader reader) {
		return new HoldingHoldingHoldingModel(reader.get(model));
	}
}
