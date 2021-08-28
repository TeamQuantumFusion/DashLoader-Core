package net.oskarstrom.dashloader.core.system.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BasicBakedModel implements BakedModel {
	public final Sprite sprite;
	public final Identifier identifier;
	public final byte[][] stateShit;

	public BasicBakedModel(Sprite sprite, Identifier identifier, byte[][] stateShit) {
		this.sprite = sprite;
		this.identifier = identifier;
		this.stateShit = stateShit;
	}

	public static BasicBakedModel create(ArrayList<BakedModel> m) {
		final byte[][] stateShit = new byte[2][2];
		stateShit[0][0] = 1;
		stateShit[1][0] = 2;
		stateShit[0][1] = 3;
		stateShit[1][1] = 4;
		final BasicBakedModel yo_mama = new BasicBakedModel(new Sprite(69), new Identifier("yo mama"), stateShit);
		m.add(yo_mama);
		return yo_mama;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BasicBakedModel that = (BasicBakedModel) o;
		if (!Objects.equals(sprite, that.sprite)) return false;
		if (!Objects.equals(identifier, that.identifier)) return false;
		return Arrays.deepEquals(stateShit, that.stateShit);
	}

	@Override
	public int hashCode() {
		int result = sprite != null ? sprite.hashCode() : 0;
		result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
		result = 31 * result + Arrays.deepHashCode(stateShit);
		return result;
	}
}
