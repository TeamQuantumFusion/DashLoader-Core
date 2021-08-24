package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Serialize;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class DashMap<E> {

	protected List<E> data;

	public DashMap(List<E> data) {
		this.data = data;
	}

	@Serialize(order = 0)
	public List<E> getData() {
		return data;
	}


	public void setData(List<E> data) {
		this.data = data;
	}

	public DashMap() {
		data = new ArrayList<>();
	}

	public DashMap(int size) {
		data = new ArrayList<>(size);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DashMap<?> dashMap = (DashMap<?>) o;
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

	public Iterator<E> iterator() {
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


	public boolean add(E e) {
		return data.add(e);
	}


	public boolean remove(Object o) {
		return data.remove(o);
	}


	public boolean containsAll(@NotNull Collection<?> c) {
		return data.containsAll(c);
	}


	public boolean addAll(@NotNull Collection<? extends E> c) {
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


	public boolean removeIf(Predicate<? super E> filter) {
		return data.removeIf(filter);
	}


	public Spliterator<E> spliterator() {
		return data.spliterator();
	}


	public Stream<E> stream() {
		return data.stream();
	}


	public Stream<E> parallelStream() {
		return data.parallelStream();
	}


	public void forEach(Consumer<? super E> action) {
		data.forEach(action);
	}
}
