package net.oskarstrom.dashloader.core.registry;

import net.oskarstrom.dashloader.core.Dashable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.StringJoiner;

@SuppressWarnings("ClassCanBeRecord") // please intellij stop saying everything can be a record
public class FactoryConstructor<F, D extends Dashable<F>> {
	private final MethodHandle constructor;
	private final Mode mode;
	//for debug
	private final Class<?> dashClass;

	private FactoryConstructor(MethodHandle constructor, Mode mode, Class<?> dashClass) {
		this.constructor = constructor;
		this.mode = mode;
		this.dashClass = dashClass;
	}

	public static <F, D extends Dashable<F>> FactoryConstructor<F, D> createConstructor(Class<? extends F> rawClass, Class<? extends D> dashClass) throws IllegalAccessException, NoSuchMethodException {
		for (Mode value : Mode.values()) {
			final Class<?>[] parameters = value.getParameters(rawClass);
			try {
				return new FactoryConstructor<>(MethodHandles.publicLookup().findStatic(dashClass, "create", MethodType.methodType(dashClass, parameters)), value, dashClass);
			} catch (NoSuchMethodException ignored) {
			}
		}
		for (Mode value : Mode.values()) {
			final Class<?>[] parameters = value.getParameters(rawClass);
			try {
				return new FactoryConstructor<>(MethodHandles.publicLookup().findConstructor(dashClass, MethodType.methodType(void.class, parameters)), value, dashClass);
			} catch (NoSuchMethodException ignored) {
			}
		}
		throw new NoSuchMethodException(Mode.FULL.getExpectedMethod(dashClass, rawClass));
	}


	public D create(F object, DashRegistry registry) {
		Object objectOut;
		try {
			objectOut = switch (mode) {
				case FULL -> constructor.invoke(object, registry);
				case OBJECT -> constructor.invoke(object);
				case EMPTY -> constructor.invoke();
			};
		} catch (Throwable throwable) {
			throw new IllegalStateException(throwable);
		}

		if (objectOut != null) {
			return (D) objectOut;
		}
		throw new IllegalStateException("Constructor for " + dashClass.getSimpleName() + " returned null.");
	}


	public enum Mode {
		FULL(true, DashRegistry.class),
		OBJECT(true),
		EMPTY(false);

		private final boolean containsSelfObject;
		private final Class<?>[] list;

		Mode(boolean containsSelfObject, Class<?>... list) {
			this.containsSelfObject = containsSelfObject;
			this.list = list;
		}

		public Class<?>[] getParameters(Class<?> object) {
			if (containsSelfObject) {
				Class<?>[] out = new Class[list.length + 1];
				out[0] = object;
				System.arraycopy(list, 0, out, 1, out.length - 1);
				return out;
			} else {
				return list.clone();
			}
		}

		public String getExpectedMethod(Class<?> dashClass, Class<?> rawClass) {
			StringBuilder expectedMethod = new StringBuilder();
			expectedMethod.append("public static ");
			expectedMethod.append(dashClass.getSimpleName());
			expectedMethod.append('(');
			appendClassParameters(getParameters(rawClass), expectedMethod);
			expectedMethod.append(')');
			return expectedMethod.toString();
		}

		private void appendClassParameters(Class<?>[] classes, StringBuilder stringBuilder) {
			final StringJoiner joiner = new StringJoiner(", ");
			for (int i = 0; i < classes.length; i++) {
				// int arg0, float arg1, Identifier arg2, etc
				joiner.add(classes[i].getSimpleName() + " arg" + i);
			}
			stringBuilder.append(joiner);
		}
	}
}
