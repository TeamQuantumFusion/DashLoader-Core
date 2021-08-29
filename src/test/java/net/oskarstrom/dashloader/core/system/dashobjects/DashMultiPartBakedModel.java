package net.oskarstrom.dashloader.core.system.dashobjects;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.system.objects.MultiPartBakedModel;
import net.oskarstrom.dashloader.core.system.objects.WeightedBakedModel;
import net.oskarstrom.dashloader.core.util.DashHelper;

@DashObject(MultiPartBakedModel.class)
@Dependencies(DashWeightedBakedModel.class)
@RegistryTag(DashModel.class)
public class DashMultiPartBakedModel implements DashModel {
	@Serialize
	public final int[] weightedBakedModels;

	public DashMultiPartBakedModel(@Deserialize("weightedBakedModels") int[] weightedBakedModels) {
		this.weightedBakedModels = weightedBakedModels;
	}

	public DashMultiPartBakedModel(MultiPartBakedModel multi, DashRegistry registry) {
		weightedBakedModels = DashHelper.addArrayToRegistry(multi.weightedBakedModels, registry);
	}

	@Override
	public MultiPartBakedModel toUndash(DashExportHandler exportHandler) {
		final WeightedBakedModel[] arrayFromRegistry = DashHelper.getArrayFromRegistry(weightedBakedModels, new WeightedBakedModel[weightedBakedModels.length], exportHandler);
		return new MultiPartBakedModel(arrayFromRegistry);
	}

}
