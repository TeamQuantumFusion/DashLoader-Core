package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

import java.util.StringJoiner;

public interface FactoryConstructor<F, D extends Dashable<F>> {
	D create(F object, DashRegistry registry);

	enum Mode {
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
			expectedMethod.append("public ");
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
