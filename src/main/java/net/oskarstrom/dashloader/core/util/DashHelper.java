package net.oskarstrom.dashloader.core.util;

import net.oskarstrom.dashloader.api.registry.DashRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DashHelper {


	public static <O, I> O nullable(I input, Function<I, O> func) {
		return input == null ? null : func.apply(input);
	}


	public static <O, I> O nullable(I input, DashRegistry registry, BiFunction<I, DashRegistry, O> func) {
		return input == null ? null : func.apply(input, registry);
	}

	public static <IO> IO nullable(IO input) {
		return nullable(input, i -> i);
	}


	public static <I, O> ArrayList<O> convertCollection(Collection<I> in, Function<I, O> func) {
		return convertCollection(in, new ArrayList<>(), func);
	}

	public static <I, OK, OV> HashMap<OK, OV> convertCollectionToMap(Collection<I> in, Function<I, Map.Entry<OK, OV>> func) {
		return convertCollectionToMap(in, new HashMap<>(), func);
	}

	public static <IK, IV, OK, OV> HashMap<OK, OV> convertMap(Map<IK, IV> in, Function<Map.Entry<IK, IV>, Map.Entry<OK, OV>> func) {
		return convertMap(in, new HashMap<>(), func);
	}

	public static <I, O> O[] convertArrays(I[] in, Function<I, O> func) {
		//noinspection unchecked
		return convertArrays(in, (O[]) new Object[in.length], func);
	}

	public static <IK, IV, O> ArrayList<O> convertMapToCollection(Map<IK, IV> in, Function<Map.Entry<IK, IV>, O> func) {
		return convertMapToCollection(in, new ArrayList<>(), func);
	}


	public static <I, O, C extends Collection<O>> C convertCollection(Collection<I> in, C out, Function<I, O> func) {
		for (var o : in) {
			out.add(func.apply(o));
		}
		return out;
	}

	public static <I, OK, OV, M extends Map<OK, OV>> M convertCollectionToMap(Collection<I> in, M out, Function<I, Map.Entry<OK, OV>> func) {
		for (var o : in) {
			final var apply = func.apply(o);
			out.put(apply.getKey(), apply.getValue());
		}
		return out;
	}

	public static <IK, IV, OK, OV, M extends Map<OK, OV>> M convertMap(Map<IK, IV> in, M out, Function<Map.Entry<IK, IV>, Map.Entry<OK, OV>> func) {
		for (var entry : in.entrySet()) {
			final var apply = func.apply(entry);
			out.put(apply.getKey(), apply.getValue());
		}
		return out;
	}


	public static <IK, IV, O, C extends Collection<O>> C convertMapToCollection(Map<IK, IV> in, C out, Function<Map.Entry<IK, IV>, O> func) {
		for (var entry : in.entrySet()) {
			out.add(func.apply(entry));
		}
		return out;
	}

	public static <I, O> O[] convertArrays(I[] in, O[] out, Function<I, O> func) {
		for (int i = 0; i < in.length; i++) {
			out[i] = func.apply(in[i]);
		}
		return out;
	}


}
