package net.oskarstrom.dashloader.core.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.oskarstrom.dashloader.api.data.Object2PointerMap;
import net.oskarstrom.dashloader.api.data.PairMap;
import net.oskarstrom.dashloader.api.data.Pointer2ObjectMap;
import net.oskarstrom.dashloader.api.data.Pointer2PointerMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class SerializerTest {

	@Test
	@DisplayName("Object2Pointer Serialization Test")
	public void testObject2Pointer() {
		SerializerTestObject.testObject(Obj2PntrData.class);
	}

	@Test
	@DisplayName("Pointer2Pointer Serialization Test")
	public void testPointer2Pointer() {
		SerializerTestObject.testObject(Pntr2PntrData.class);
	}

	@Test
	@DisplayName("Pointer2Object Serialization Test")
	public void testPointer2Object() {
		SerializerTestObject.testObject(Pntr2ObjData.class);
	}

	@Test
	@DisplayName("PairMap Serialization Test")
	public void testPairMap() {
		SerializerTestObject.testObject(PairMapData.class);
	}


	public static class Obj2PntrData implements SerializerTestObject.DashTestObject {
		@Serialize(order = 0)
		public Object2PointerMap<Integer> data;

		public Obj2PntrData(@Deserialize("data") Object2PointerMap<Integer> data) {
			this.data = data;
		}

		@Override
		public Object getData() {
			return data;
		}
	}

	public static class Pntr2ObjData implements SerializerTestObject.DashTestObject {
		@Serialize(order = 0)
		public Pointer2ObjectMap<Integer> data;

		public Pntr2ObjData(@Deserialize("data") Pointer2ObjectMap<Integer> data) {
			this.data = data;
		}

		@Override
		public Object getData() {
			return data;
		}
	}

	public static class Pntr2PntrData implements SerializerTestObject.DashTestObject {
		@Serialize(order = 0)
		public Pointer2PointerMap data;

		public Pntr2PntrData(@Deserialize("data") Pointer2PointerMap data) {
			this.data = data;
		}


		@Override
		public Object getData() {
			return data;
		}
	}

	public static class PairMapData implements SerializerTestObject.DashTestObject {
		@Serialize(order = 0)
		public PairMap<Integer, String> data;

		public PairMapData(@Deserialize("data") PairMap<Integer, String> data) {
			this.data = data;
		}


		@Override
		public Object getData() {
			return data;
		}

	}
}
