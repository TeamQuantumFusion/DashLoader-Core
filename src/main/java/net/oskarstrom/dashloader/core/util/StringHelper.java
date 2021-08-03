package net.oskarstrom.dashloader.core.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

public class StringHelper {
	private static final Pattern REGEX = Pattern.compile("\\{}");

	public static String of(String str, Object... parameters) {
		final Iterator<Object> iterator = Arrays.stream(parameters).iterator();
		return REGEX.matcher(str).replaceAll(matchResult -> {
			return iterator.hasNext() ? iterator.next().toString() : matchResult.group();
		});
	}

}
