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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WeightedBakedModel that = (WeightedBakedModel) o;

		if (weight != that.weight) return false;
		return model != null ? model.equals(that.model) : that.model == null;
	}

	@Override
	public int hashCode() {
		int result = weight;
		result = 31 * result + (model != null ? model.hashCode() : 0);
		return result;
	}
}
