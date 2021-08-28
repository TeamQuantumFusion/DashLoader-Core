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
import net.oskarstrom.dashloader.core.system.objects.BasicBakedModel;

@DashObject(BasicBakedModel.class)
@RegistryTag(BakedModel.class)
@Dependencies(DashIdentifier.class)
public class DashBasicBakedModel implements DashModel, Dashable<BasicBakedModel> {
	@Serialize
	public final int sprite;
	@Serialize
	public final int identifier;
	@Serialize
	public final byte[][] stateShit;

	public DashBasicBakedModel(@Deserialize("sprite") int sprite, @Deserialize("identifier") int identifier, @Deserialize("stateShit") byte[][] stateShit) {
		this.sprite = sprite;
		this.identifier = identifier;
		this.stateShit = stateShit;
	}

	public DashBasicBakedModel(BasicBakedModel basicBakedModel, DashRegistry registry) {
		sprite = registry.add(basicBakedModel.sprite);
		stateShit = basicBakedModel.stateShit;
		identifier = registry.add(basicBakedModel.identifier);
	}

	@Override
	public BasicBakedModel toUndash(DashExportHandler exportHandler) {
		return new BasicBakedModel(exportHandler.get(sprite), exportHandler.get(identifier), stateShit);
	}


}
