package net.oskarstrom.dashloader.core.data;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class ActiveJTests {


	public static <O> void test(O object, int size) {
		BinarySerializer<O> builder = SerializerBuilder.create().build(object.getClass());
		byte[] data = new byte[size];
		builder.encode(data, 0, object);
		builder.decode(data, 0);
	}

	@Nested
	public class Basics {
		@Test
		public void intTest() {
			test(new intClass(505), 300);
		}

		@Test
		public void byteTest() {
			test(new byteClass((byte) 4), 300);
		}

		@Test
		public void shortTest() {
			test(new shortClass((short) 69), 300);
		}

		@Test
		public void longTest() {
			test(new longClass(420), 300);
		}

		@Test
		public void floatTest() {
			test(new floatClass(2.3f), 300);
		}

		@Test
		public void doubleTest() {
			test(new doubleClass(3.2), 300);
		}

		@Test
		public void booleanTest() {
			test(new booleanClass(true), 300);
		}

		@Test
		public void charTest() {
			test(new charClass('f'), 300);
		}

		public static class intClass {
			@Serialize
			public int value;

			public intClass(@Deserialize("value") int value) {
				this.value = value;
			}
		}

		public static class byteClass {
			@Serialize
			public byte value;

			public byteClass(@Deserialize("value") byte value) {
				this.value = value;
			}
		}

		public static class shortClass {
			@Serialize
			public short value;

			public shortClass(@Deserialize("value") short value) {
				this.value = value;
			}
		}

		public static class longClass {
			@Serialize
			public long value;

			public longClass(@Deserialize("value") long value) {
				this.value = value;
			}
		}

		public static class floatClass {
			@Serialize
			public float value;

			public floatClass(@Deserialize("value") float value) {
				this.value = value;
			}
		}

		public static class doubleClass {
			@Serialize
			public double value;

			public doubleClass(@Deserialize("value") double value) {
				this.value = value;
			}
		}

		public static class booleanClass {
			@Serialize
			public boolean value;

			public booleanClass(@Deserialize("value") boolean value) {
				this.value = value;
			}
		}

		public static class charClass {
			@Serialize
			public char value;

			public charClass(@Deserialize("value") char value) {
				this.value = value;
			}
		}
	}

	@Nested
	public class MultiArray {

		public static <K> Supplier<K[]> arr(Supplier<K> create) {
			return () -> {
				int amount = 2;
				K[] strings = (K[]) new Object[amount];
				for (int i = 0; i < amount; i++) {
					strings[i] = create.get();
				}
				return strings;
			};
		}

		@Test
		public void Array1Test() {
			test(new Array1(new String[0]), 300);
		}

		@Test
		public void Array2Test() {
			test(new Array2(new String[0][0]), 300);
		}

		@Test
		public void Array3Test() {
			test(new Array3(new String[0][0][0]), 300);
		}

		@Test
		public void Array4Test() {

			test(new Array4(new String[0][0][0][0]), 300);
		}

		@Test
		public void Array5Test() {
			test(new Array5(new String[0][0][0][0][0]), 300);
		}

		@Test
		public void Array6Test() {

			test(new Array6(new String[0][0][0][0][0][0]), 300);
		}

		@Test
		public void Array7Test() {
			test(new Array7(new String[0][0][0][0][0][0][0]), 300);
		}

		@Test
		public void Array8Test() {
			test(new Array8(new String[0][0][0][0][0][0][0][0]), 300);
		}

		public static class Array1 {
			@Serialize
			public String[] value;

			public Array1(@Deserialize("value") String[] value) {
				this.value = value;
			}
		}

		public static class Array2 {
			@Serialize
			public String[][] value;

			public Array2(@Deserialize("value") String[][] value) {
				this.value = value;
			}
		}

		public static class Array3 {
			@Serialize
			public String[][][] value;

			public Array3(@Deserialize("value") String[][][] value) {
				this.value = value;
			}
		}

		public static class Array4 {
			@Serialize
			public String[][][][] value;

			public Array4(@Deserialize("value") String[][][][] value) {
				this.value = value;
			}
		}

		public static class Array5 {
			@Serialize
			public String[][][][][] value;

			public Array5(@Deserialize("value") String[][][][][] value) {
				this.value = value;
			}
		}

		public static class Array6 {
			@Serialize
			public String[][][][][][] value;

			public Array6(@Deserialize("value") String[][][][][][] value) {
				this.value = value;
			}
		}

		public static class Array7 {
			@Serialize
			public String[][][][][][][] value;

			public Array7(@Deserialize("value") String[][][][][][][] value) {
				this.value = value;
			}
		}

		public static class Array8 {
			@Serialize
			public String[][][][][][][][] value;

			public Array8(@Deserialize("value") String[][][][][][][][] value) {
				this.value = value;
			}
		}

	}

}
