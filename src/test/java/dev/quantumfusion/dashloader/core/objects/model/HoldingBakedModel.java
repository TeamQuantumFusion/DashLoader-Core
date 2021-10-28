package dev.quantumfusion.dashloader.core.objects.model;

public record HoldingBakedModel(BakedModel basicModel,
								int pos) implements Model {
}
