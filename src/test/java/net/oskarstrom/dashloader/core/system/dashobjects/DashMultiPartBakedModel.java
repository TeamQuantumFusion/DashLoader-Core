package net.oskarstrom.dashloader.core.system.dashobjects;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.annotations.DashObject;
import net.oskarstrom.dashloader.api.annotations.Dependencies;
import net.oskarstrom.dashloader.api.annotations.RegistryTag;
import net.oskarstrom.dashloader.api.registry.DashExportHandler;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.core.system.objects.BakedModel;
import net.oskarstrom.dashloader.core.system.objects.MultiPartBakedModel;
import net.oskarstrom.dashloader.core.util.DashHelper;

@DashObject(MultiPartBakedModel.class)
@Dependencies(DashWeightedBakedModel.class)
@RegistryTag(BakedModel.class)
public class DashMultiPartBakedModel implements DashModel, Dashable<MultiPartBakedModel> {
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
		return new MultiPartBakedModel(DashHelper.getArrayFromRegistry(weightedBakedModels, exportHandler));
	}

}
