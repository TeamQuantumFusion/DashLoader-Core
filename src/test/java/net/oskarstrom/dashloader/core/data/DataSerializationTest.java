package net.oskarstrom.dashloader.core.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.core.TestUtils;
import net.oskarstrom.dashloader.core.registry.Pointer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class DataSerializationTest {

	private static final int standardPointer = Pointer.parsePointer(420, (byte) 69);

	@Test
	@DisplayName("Object2Pointer Serialization Test")
	public void testObject2Pointer() {
		TestUtils.test(Obj2PntrData.class);
		Object2PointerMap<Integer> object2PointerMap = new Object2PointerMap<>();
		object2PointerMap.add(Object2PointerMap.Entry.of(420, standardPointer));
	}

	@Test
	@DisplayName("Pointer2Pointer Serialization Test")
	public void testPointer2Pointer() {
		TestUtils.test(Pntr2PntrData.class);
		Pointer2PointerMap object2PointerMap = new Pointer2PointerMap();
		object2PointerMap.add(Pointer2PointerMap.Entry.of(standardPointer, standardPointer));
	}

	@Test
	@DisplayName("Pointer2Object Serialization Test")
	public void testPointer2Object() {
		TestUtils.test(Pntr2ObjData.class);
		Pointer2ObjectMap<Integer> object2PointerMap = new Pointer2ObjectMap<>();
		object2PointerMap.add(Pointer2ObjectMap.Entry.of(standardPointer, 420));
	}

	@Test
	@DisplayName("PairMap Serialization Test")
	public void testPairMap() {
		TestUtils.test(PairMapData.class);
		PairMap<Integer, Integer> object2PointerMap = new PairMap<>();
		object2PointerMap.add(PairMap.Entry.of(420, 420));
	}


	public static class Obj2PntrData {
		@Serialize(order = 0)
		public Object2PointerMap<Integer> data;

		public Obj2PntrData(@Deserialize("data") Object2PointerMap<Integer> data) {
			this.data = data;
		}

	}

	public static class Pntr2ObjData {
		@Serialize(order = 0)
		public Pointer2ObjectMap<Integer> data;

		public Pntr2ObjData(@Deserialize("data") Pointer2ObjectMap<Integer> data) {
			this.data = data;
		}

	}

	public static class Pntr2PntrData {
		@Serialize(order = 0)
		public Pointer2PointerMap data;

		public Pntr2PntrData(@Deserialize("data") Pointer2PointerMap data) {
			this.data = data;
		}


	}

	public static class PairMapData {
		@Serialize(order = 0)
		public PairMap<Integer, String> data;

		public PairMapData(@Deserialize("data") PairMap<Integer, String> data) {
			this.data = data;
		}
	}
}
