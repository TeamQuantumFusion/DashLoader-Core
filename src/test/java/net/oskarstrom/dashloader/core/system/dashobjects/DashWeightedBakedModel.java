package net.oskarstrom.dashloader.core.system.dashobjects;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.system.objects.WeightedBakedModel;

@DashObject(WeightedBakedModel.class)
@Dependencies(DashBasicBakedModel.class)
@RegistryTag(DashModel.class)
public class DashWeightedBakedModel implements DashModel {
	@Serialize
	public final int weight;
	@Serialize
	public final int model;

	public DashWeightedBakedModel(@Deserialize("weight") int weight, @Deserialize("model") int model) {
		this.weight = weight;
		this.model = model;
	}

	public DashWeightedBakedModel(WeightedBakedModel weightedBakedModel, DashRegistry registry) {
		model = registry.add(weightedBakedModel.model);
		weight = weightedBakedModel.weight;
	}


	@Override
	public WeightedBakedModel toUndash(DashExportHandler exportHandler) {
		return new WeightedBakedModel(weight, exportHandler.get(model));
	}
}
