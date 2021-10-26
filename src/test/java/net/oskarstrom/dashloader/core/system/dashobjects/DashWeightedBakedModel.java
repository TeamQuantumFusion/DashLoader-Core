package net.oskarstrom.dashloader.core.system.dashobjects;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.system.objects.WeightedBakedModel;

@Data
@DashObject(WeightedBakedModel.class)
@Dependencies(DashBasicBakedModel.class)
@RegistryTag(DashModel.class)
public record DashWeightedBakedModel(int weight, int model) implements DashModel {
	public static DashWeightedBakedModel create(WeightedBakedModel weightedBakedModel, DashRegistry registry) {
		return new DashWeightedBakedModel(weightedBakedModel.weight, registry.add(weightedBakedModel.model));
	}


	@Override
	public WeightedBakedModel toUndash(DashExportHandler exportHandler) {
		return new WeightedBakedModel(weight, exportHandler.get(model));
	}
}
