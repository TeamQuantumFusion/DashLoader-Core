package net.oskarstrom.dashloader.core.system.dashobjects;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import net.oskarstrom.dashloader.core.annotations.DashObject;
import net.oskarstrom.dashloader.core.annotations.Dependencies;
import net.oskarstrom.dashloader.core.annotations.RegistryTag;
import net.oskarstrom.dashloader.core.registry.DashExportHandler;
import net.oskarstrom.dashloader.core.registry.DashRegistry;
import net.oskarstrom.dashloader.core.system.objects.BasicBakedModel;

import java.util.Arrays;
import java.util.Objects;

@DashObject(BasicBakedModel.class)
@RegistryTag(DashModel.class)
@Dependencies(DashIdentifier.class)
@Data
public record DashBasicBakedModel(int sprite, int identifier, byte[][] stateShit) implements DashModel {
	public static DashBasicBakedModel create(BasicBakedModel basicBakedModel, DashRegistry registry) {
		return new DashBasicBakedModel(registry.add(basicBakedModel.sprite), registry.add(basicBakedModel.identifier), basicBakedModel.stateShit);
	}

	@Override
	public BasicBakedModel toUndash(DashExportHandler exportHandler) {
		return new BasicBakedModel(exportHandler.get(sprite), exportHandler.get(identifier), stateShit);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DashBasicBakedModel that = (DashBasicBakedModel) o;
		return sprite == that.sprite && identifier == that.identifier && Arrays.deepEquals(stateShit, that.stateShit);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(sprite, identifier);
		result = 31 * result + Arrays.deepHashCode(stateShit);
		return result;
	}
}
