package net.oskarstrom.dashloader.core.system.objects;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiPartBakedModel implements BakedModel {
	public final WeightedBakedModel[] weightedBakedModels;

	public MultiPartBakedModel(WeightedBakedModel[] weightedBakedModels) {
		this.weightedBakedModels = weightedBakedModels;
	}

	public static MultiPartBakedModel create(ArrayList<BakedModel> m) {
		final WeightedBakedModel[] weightedBakedModels = new WeightedBakedModel[8];
		weightedBakedModels[0] = WeightedBakedModel.create(m);
		weightedBakedModels[1] = WeightedBakedModel.create(m);
		weightedBakedModels[2] = WeightedBakedModel.create(m);
		weightedBakedModels[3] = WeightedBakedModel.create(m);
		weightedBakedModels[4] = WeightedBakedModel.create(m);
		weightedBakedModels[5] = WeightedBakedModel.create(m);
		weightedBakedModels[6] = WeightedBakedModel.create(m);
		weightedBakedModels[7] = WeightedBakedModel.create(m);
		final MultiPartBakedModel multiPartBakedModel = new MultiPartBakedModel(weightedBakedModels);
		m.add(multiPartBakedModel);
		return multiPartBakedModel;
	}

	@Override
	public boolean equals(Object o) {


		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MultiPartBakedModel that = (MultiPartBakedModel) o;

		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(weightedBakedModels, that.weightedBakedModels);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(weightedBakedModels);
	}
}
