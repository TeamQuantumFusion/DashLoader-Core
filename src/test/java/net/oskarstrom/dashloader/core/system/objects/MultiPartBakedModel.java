package net.oskarstrom.dashloader.core.system.objects;

import java.util.ArrayList;

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

}
