package net.oskarstrom.dashloader.api.data;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class DashMap<E> implements Collection<E> {

	@Serialize(order = 0)
	public List<E> data;

	public DashMap(@Deserialize("data") List<E> data) {
		this.data = data;
	}

	public DashMap() {
		data = new ArrayList<>();
	}

	public DashMap(int size) {
		data = new ArrayList<>(size);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return data.contains(o);
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return data.iterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return data.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return data.remove(o);
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends E> c) {
		return data.addAll(c);
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return data.removeAll(c);
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return data.retainAll(c);
	}

	@Override
	public void clear() {
		data.clear();
	}


}
