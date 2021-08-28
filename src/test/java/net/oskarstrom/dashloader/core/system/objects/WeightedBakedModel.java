package net.oskarstrom.dashloader.core.system.objects;

import java.util.ArrayList;

public class WeightedBakedModel implements BakedModel {
	public final int weight;
	public final BasicBakedModel model;

	public WeightedBakedModel(int weight, BasicBakedModel model) {
		this.weight = weight;
		this.model = model;
	}

	public static WeightedBakedModel create(ArrayList<BakedModel> m) {
		final WeightedBakedModel weightedBakedModel = new WeightedBakedModel(69, BasicBakedModel.create(m));
		m.add(weightedBakedModel);
		return weightedBakedModel;
	}

}
