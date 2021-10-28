package dev.quantumfusion.dashloader.core.objects.model;


import dev.quantumfusion.dashloader.core.objects.Identifier;

public record BakedModel(int image, Identifier identifier,
						 String anotherThing) implements Model {
}
