package net.oskarstrom.dashloader.core.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Object2PointerMap<O> {
	@Serialize
	public final List<Entry<O>> data;

	public Object2PointerMap(@Deserialize("data") List<Entry<O>> data) {
		this.data = data;
	}

	public Object2PointerMap() {
		data = new ArrayList<>();
	}


	public Object2PointerMap(int size) {
		data = new ArrayList<>(size);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Object2PointerMap<?> dashMap = (Object2PointerMap<?>) o;
		return Objects.equals(data, dashMap.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}

	public int size() {
		return data.size();
	}


	public boolean isEmpty() {
		return data.isEmpty();
	}


	public boolean contains(Object o) {
		return data.contains(o);
	}

	@NotNull

	public Iterator<Entry<O>> iterator() {
		return data.iterator();
	}

	@NotNull

	public Object[] toArray() {
		return data.toArray();
	}

	@NotNull

	public <T> T[] toArray(@NotNull T[] a) {
		return data.toArray(a);
	}


	public boolean add(Entry<O> e) {
		return data.add(e);
	}


	public boolean remove(Object o) {
		return data.remove(o);
	}


	public boolean containsAll(@NotNull Collection<?> c) {
		return data.containsAll(c);
	}


	public boolean addAll(@NotNull Collection<? extends Entry<O>> c) {
		return data.addAll(c);
	}


	public boolean removeAll(@NotNull Collection<?> c) {
		return data.removeAll(c);
	}


	public boolean retainAll(@NotNull Collection<?> c) {
		return data.retainAll(c);
	}


	public void clear() {
		data.clear();
	}


	public <T> T[] toArray(IntFunction<T[]> generator) {
		return data.toArray(generator);
	}


	public boolean removeIf(Predicate<? super Entry<O>> filter) {
		return data.removeIf(filter);
	}


	public Spliterator<Entry<O>> spliterator() {
		return data.spliterator();
	}


	public Stream<Entry<O>> stream() {
		return data.stream();
	}


	public Stream<Entry<O>> parallelStream() {
		return data.parallelStream();
	}


	public void forEach(Consumer<? super Entry<O>> action) {
		data.forEach(action);
	}

	public static class Entry<O> implements DashEntry<O, Integer> {
		@Serialize(order = 0)
		public final O key;
		@Serialize(order = 1)
		public final int value;

		public Entry(@Deserialize("key") O key,
					 @Deserialize("value") int value) {
			this.key = key;
			this.value = value;
		}

		public static <O> Entry<O> of(O key, int value) {
			return new Entry<>(key, value);
		}

		@Override
		public O getKey() {
			return key;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Entry<?> entry = (Entry<?>) o;
			return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}
	}
}
