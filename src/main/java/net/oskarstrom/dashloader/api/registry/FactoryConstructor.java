package net.oskarstrom.dashloader.api.registry;

import net.oskarstrom.dashloader.api.Dashable;

import java.util.Arrays;
import java.util.Iterator;

public interface FactoryConstructor<F, D extends Dashable<F>> {
	D create(F object, DashRegistry registry);


	enum Mode {
		FULL(true, DashRegistry.class),
		OBJECT(true),
		EMPTY(false);


		private boolean containsSelfObject;
		private Class<?>[] list;

		Mode(boolean containsSelfObject, Class<?>... list) {
			this.containsSelfObject = containsSelfObject;
			this.list = list;
		}

		public Class<?>[] getParameters(Class<?> object) {
			Class<?>[] out = new Class[(containsSelfObject ? 1 : 0) + list.length];
			if (containsSelfObject) {
				out[0] = object;
				System.arraycopy(list, 0, out, 1, out.length - 1);
			} else {
				System.arraycopy(list, 0, out, 0, out.length);
			}
			return out;
		}


		public String getExpectedMethod(Class<?> dashClass, Class<?> rawClass) {
			StringBuilder expectedMethod = new StringBuilder();
			expectedMethod.append("public ");
			expectedMethod.append(dashClass.getSimpleName());
			expectedMethod.append('(');
			printClasses(getParameters(rawClass), expectedMethod);
			expectedMethod.append(')');
			return expectedMethod.toString();
		}

		private void printClasses(Class<?>[] classes, StringBuilder stringBuilder) {
			for (Iterator<Class<?>> iterator = Arrays.stream(classes).iterator(); iterator.hasNext(); ) {
				Class<?> aClass = iterator.next();
				final String simpleName = aClass.getSimpleName();
				stringBuilder.append(simpleName).append(' ').append(Character.toLowerCase(simpleName.charAt(0))).append(simpleName.substring(1));
				if (iterator.hasNext()) {
					stringBuilder.append(", ");
				}
			}
		}
	}
}
