package net.oskarstrom.dashloader.core.data;

import io.activej.serializer.BinarySerializer;
import io.activej.serializer.SerializerBuilder;
import io.activej.serializer.stream.StreamInput;
import io.activej.serializer.stream.StreamOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SerializerTestObject {


	public static <C> C createObject(Class<C> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		System.out.println(clazz.getSimpleName());
		try {
			return fromConstructor(clazz.getDeclaredConstructor());
		} catch (NoSuchMethodException e) {
			return fromConstructor((Constructor<C>) clazz.getDeclaredConstructors()[0]);
		}
	}

	private static <C> C fromConstructor(Constructor<C> constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		final Class<?>[] constructorParameters = constructor.getParameterTypes();
		Object[] parameters = new Object[constructorParameters.length];
		for (int i = 0; i < constructorParameters.length; i++) {
			Class<?> parameterType = constructorParameters[i];
			if (parameterType == int.class || parameterType == Integer.class) {
				parameters[i] = 69;
			} else if (parameterType == boolean.class || parameterType == Boolean.class) {
				parameters[i] = true;
			} else {
				parameters[i] = createObject(parameterType);
			}
		}
		return constructor.newInstance(parameters);
	}


	public static <O extends DashTestObject> void testObject(Class<O> object) {
		try {
			assertTrue(testObjectInternal(createObject(object)));
			return;
		} catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		fail();
	}

	public static <O extends DashTestObject> boolean testObjectInternal(O object) throws IOException {
		//create serializer
		final BinarySerializer<O> build = SerializerBuilder.create().build(object.getClass());
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final StreamOutput streamOutput = StreamOutput.create(output);
		streamOutput.serialize(build, object);
		streamOutput.close();
		final O deserialize = StreamInput.create(new ByteArrayInputStream(output.toByteArray())).deserialize(build);
		return object.getData().equals(deserialize.getData());
	}

	public interface DashTestObject {
		Object getData();
	}

}
