package net.oskarstrom.dashloader.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderHelper {
	private static Accessor accessor;

	public static void setAccessor(ClassLoader classLoader) {
		accessor = new Accessor(classLoader);
	}


	private static void checkIfNull() {
		if (accessor == null)
			throw new NullPointerException("Accessor ClassLoader is null");
	}

	public static Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		checkIfNull();
		return accessor.loadClass(name, resolve);
	}

	public static Object getClassLoadingLock(String className) {
		checkIfNull();
		return accessor.getClassLoadingLock(className);
	}

	public static Class<?> findClass(String name) throws ClassNotFoundException {
		checkIfNull();
		return accessor.findClass(name);
	}

	public static Class<?> findClass(String moduleName, String name) {
		checkIfNull();
		return accessor.findClass(moduleName, name);
	}

	public static URL findResource(String moduleName, String name) throws IOException {
		checkIfNull();
		return accessor.findResource(moduleName, name);
	}

	public static URL findResource(String name) {
		checkIfNull();
		return accessor.findResource(name);
	}

	public static Enumeration<URL> findResources(String name) throws IOException {
		checkIfNull();
		return accessor.findResources(name);
	}

	public static Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) {
		checkIfNull();
		return accessor.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
	}

	public static Package[] getPackages() {
		checkIfNull();
		return accessor.getPackages();
	}

	public static String findLibrary(String libname) {
		checkIfNull();
		return accessor.findLibrary(libname);
	}

	public static Class<?> defineClass(String name, byte[] b, int off, int len) throws ClassFormatError {
		checkIfNull();
		return accessor.defineClassAccess(name, b, off, len);
	}

	private static class Accessor extends ClassLoader {
		private Accessor(ClassLoader parent) {
			super(parent);
		}

		protected Class<?> defineClassAccess(String name, byte[] b, int off, int len) throws ClassFormatError {
			return super.defineClass(name, b, off, len);
		}


		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			return super.loadClass(name, resolve);
		}

		@Override
		protected Object getClassLoadingLock(String className) {
			return super.getClassLoadingLock(className);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}

		@Override
		protected Class<?> findClass(String moduleName, String name) {
			return super.findClass(moduleName, name);
		}

		@Override
		protected URL findResource(String moduleName, String name) throws IOException {
			return super.findResource(moduleName, name);
		}


		@Override
		protected URL findResource(String name) {
			return super.findResource(name);
		}

		@Override
		protected Enumeration<URL> findResources(String name) throws IOException {
			return super.findResources(name);
		}

		@Override
		protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) {
			return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
		}

		@Override
		protected Package[] getPackages() {
			return super.getPackages();
		}


		@Override
		protected String findLibrary(String libname) {
			return super.findLibrary(libname);
		}

	}


}
