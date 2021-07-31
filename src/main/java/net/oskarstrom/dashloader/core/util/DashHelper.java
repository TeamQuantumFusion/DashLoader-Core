package net.oskarstrom.dashloader.core.util;

import net.oskarstrom.dashloader.api.registry.DashRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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

	public static <I, O> Collection<O> convertCollection(Collection<I> in, Function<I, O> func, Supplier<Collection<O>> createFunc) {
		Collection<O> temp = createFunc.get();
		for (I o : in) {
			temp.add(func.apply(o));
		}
		in.clear();
		return temp;
	}


	public static <I, OK, OV> Map<OK, OV> convertCollectionToMap(Collection<I> in, Function<I, Map.Entry<OK, OV>> func, Supplier<Map<OK, OV>> createFunc) {
		Map<OK, OV> temp = createFunc.get();
		for (I o : in) {
			final Map.Entry<OK, OV> apply = func.apply(o);
			temp.put(apply.getKey(), apply.getValue());
		}
		in.clear();
		return temp;
	}


	public static <IK, IV, O> Collection<O> convertMapToCollection(Map<IK, IV> in, Function<Map.Entry<IK, IV>, O> func, Supplier<Collection<O>> createFunc) {
		Collection<O> temp = createFunc.get();
		for (var ikivEntry : in.entrySet()) {
			temp.add(func.apply(ikivEntry));
		}
		in.clear();
		return temp;
	}

	public static <V, OV> void convertArrays(V[] in, OV[] out, Function<V, OV> func) {
		for (int i = 0; i < in.length; i++) {
			out[i] = func.apply(in[i]);
		}
	}


}
