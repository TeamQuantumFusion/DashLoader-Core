package dev.quantumfusion.dashloader.core.api;

import dev.quantumfusion.dashloader.core.Dashable;
import dev.quantumfusion.dashloader.core.registry.DashRegistryWriter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.StringJoiner;

public class DashConstructor<R, D extends Dashable<R>> {
	public final Class<?> dashClass;
	private final MethodHandle constructor;
	private final Mode mode;

	public DashConstructor(MethodHandle constructor, Mode mode, Class<?> dashClass) {
		this.constructor = constructor;
		this.mode = mode;
		this.dashClass = dashClass;
	}

	@SuppressWarnings("unchecked")
	public static <R, D extends Dashable<R>> DashConstructor<R, D> create(Class<?> dashClass, Class<?> rawClass) {
		try {
			return createConstructor((Class<? extends D>) dashClass, (Class<? extends R>) rawClass);
		} catch (IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static <R, D extends Dashable<R>> DashConstructor<R, D> createConstructor(Class<? extends D> dashClass, Class<? extends R> rawClass) throws IllegalAccessException, NoSuchMethodException {
		for (Mode mode : Mode.values()) {
			final Class<?>[] parameters = mode.getParameters(rawClass);
			try {
				final MethodType type = MethodType.methodType(dashClass, parameters);
				System.out.println(type);
				return new DashConstructor<>(MethodHandles.publicLookup().findStatic(dashClass, "create", type), mode, dashClass);
			} catch (NoSuchMethodException ignored) {
			}
			try {
				final MethodType type = MethodType.methodType(void.class, parameters);
				System.out.println(type);
				return new DashConstructor<>(MethodHandles.publicLookup().findConstructor(dashClass, type), mode, dashClass);
			} catch (NoSuchMethodException ignored) {
			}
		}

		throw new NoSuchMethodException(Mode.FULL.getExpectedMethod(dashClass, rawClass));
	}

	public D invoke(R raw, DashRegistryWriter registry) {
		Object objectOut;
		try {
			objectOut = switch (mode) {
				case FULL -> constructor.invoke(raw, registry);
				case OBJECT -> constructor.invoke(raw);
				case EMPTY -> constructor.invoke();
			};
		} catch (Throwable throwable) {
			throw new IllegalStateException(throwable);
		}

		if (objectOut != null) return (D) objectOut;
		throw new IllegalStateException("Constructor for " + dashClass.getSimpleName() + " returned null.");
	}

	public enum Mode {
		FULL(true, DashRegistryWriter.class),
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
