package net.oskarstrom.dashloader.core.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

public class StringHelper {
	private static final Pattern REGEX = Pattern.compile("\\{}");

	public static String of(String str, Object... parameters) {
		final Iterator<String> iterator = Arrays.stream(parameters).map(Object::toString).iterator();
		return REGEX.matcher(str).replaceAll(matchResult -> iterator.hasNext() ? iterator.next() : matchResult.group());
	}

}
