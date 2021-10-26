package net.oskarstrom.dashloader.core.util;

import java.security.SecureClassLoader;

public class ClassLoaderHelper {
	public static Accessor accessor;

	private static void checkIfNull() {
		if (accessor == null)
			throw new NullPointerException("Accessor ClassLoader is null");
	}

	public static void init() {
		init(Thread.currentThread().getContextClassLoader());
	}

	public static void init(ClassLoader loader) {
		accessor = new Accessor(loader);
	}

	public static Class<?> findClass(String name) throws ClassNotFoundException {
		checkIfNull();
		return accessor.findClass(name);
	}

	public static Class<?> defineClass(String name, byte[] b, int off, int len) throws ClassFormatError {
		checkIfNull();
		return accessor.defineClassAccess(name, b, off, len);
	}

	public static class Accessor extends SecureClassLoader {
		public Accessor(ClassLoader parent) {
			super(parent);
		}

		protected Class<?> defineClassAccess(String name, byte[] b, int off, int len) throws ClassFormatError {
			return super.defineClass(name, b, off, len);
		}


		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}
	}


}
