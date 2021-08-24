package net.oskarstrom.dashloader.core.registry;

import net.oskarstrom.dashloader.api.Dashable;
import net.oskarstrom.dashloader.api.registry.DashRegistry;
import net.oskarstrom.dashloader.api.registry.FactoryConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@SuppressWarnings("ClassCanBeRecord") // please intellij stop saying everything can be a record
public class FactoryConstructorImpl<F, D extends Dashable<F>> implements FactoryConstructor<F, D> {
	private final MethodHandle constructor;
	private final FactoryConstructor.Mode mode;
	//for debug
	private final Class<?> dashClass;

	private FactoryConstructorImpl(MethodHandle constructor, Mode mode, Class<?> dashClass) {
		this.constructor = constructor;
		this.mode = mode;
		this.dashClass = dashClass;
	}

	public static <F, D extends Dashable<F>> FactoryConstructor<F, D> createConstructor(Class<? extends F> rawClass, Class<? extends D> dashClass) throws IllegalAccessException, NoSuchMethodException {
		for (Mode value : Mode.values()) {
			final Class<?>[] parameters = value.getParameters(rawClass);
			try {
				return new FactoryConstructorImpl<>(MethodHandles.publicLookup().findConstructor(dashClass, MethodType.methodType(void.class, parameters)), value, dashClass);
			} catch (NoSuchMethodException ignored) {
			}
		}
		throw new NoSuchMethodException(Mode.FULL.getExpectedMethod(dashClass, rawClass));
	}


	@Override
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
		throw new IllegalStateException("Constructor returned null.");
	}


}
