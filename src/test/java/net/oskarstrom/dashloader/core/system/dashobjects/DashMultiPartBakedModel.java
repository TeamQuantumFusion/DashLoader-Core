package net.oskarstrom.dashloader.core.system.dashobjects;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.system.objects.MultiPartBakedModel;
import net.oskarstrom.dashloader.core.system.objects.WeightedBakedModel;
import net.oskarstrom.dashloader.core.util.DashHelper;

import java.util.Arrays;

@Data
@DashObject(MultiPartBakedModel.class)
@Dependencies(DashWeightedBakedModel.class)
@RegistryTag(DashModel.class)
public record DashMultiPartBakedModel(int[] weightedBakedModels) implements DashModel {

	public static DashMultiPartBakedModel create(MultiPartBakedModel multi, DashRegistry registry) {
		return new DashMultiPartBakedModel(DashHelper.addArrayToRegistry(multi.weightedBakedModels, registry));
	}

	@Override
	public MultiPartBakedModel toUndash(DashExportHandler exportHandler) {
		return new MultiPartBakedModel(DashHelper.getArrayFromRegistry(weightedBakedModels, new WeightedBakedModel[weightedBakedModels.length], exportHandler));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DashMultiPartBakedModel that = (DashMultiPartBakedModel) o;
		return Arrays.equals(weightedBakedModels, that.weightedBakedModels);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(weightedBakedModels);
	}
}
