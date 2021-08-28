package net.oskarstrom.dashloader.core.util;

import it.unimi.dsi.fastutil.objects.ObjectIterators;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UnsafeList<E> implements List<E> {
	protected E[] elements;
	private int size;


	public UnsafeList() {
		//noinspection unchecked
		this.elements = (E[]) new Object[1];
		this.size = 0;
	}

	public UnsafeList(int size) {
		this.elements = (E[]) new Object[size];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		for (Object element : elements)
			if (o.equals(element))
				return true;

		return false;
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return Arrays.stream(elements).iterator();
	}


	@Override
	public Object[] toArray() {
		return elements;
	}


	@Override
	public <T> T[] toArray(T @NotNull [] a) {
		if (a.length < size) {
			//noinspection unchecked
			return (T[]) Arrays.copyOf(elements, size, a.getClass());
		}

		System.arraycopy(elements, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	@Override
	public boolean add(E e) {
		ensureSize(size + 1);
		elements[size++] = e;
		return true;
	}

	private void ensureSize(int size) {
		if (elements.length < size) {
			//noinspection unchecked
			E[] newArray = (E[]) new Object[elements.length * 2];
			System.arraycopy(elements, 0, newArray, 0, elements.length);
			elements = newArray;
		}
	}

	@Override
	public boolean remove(Object o) {
		for (int i = 0, elementsLength = elements.length; i < elementsLength; i++) {
			if (o.equals(elements[i])) {
				elements[i] = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends E> c) {
		for (E e : c)
			add(e);
		return true;
	}

	@Override
	public boolean addAll(int index, @NotNull Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		for (int i = 0, elementsLength = elements.length; i < elementsLength; i++)
			elements[i] = null;
	}

	@Override
	public E get(int index) {
		return elements[index];
	}

	@Override
	public E set(int index, E element) {
		ensureSize(index);
		return elements[index] = element;
	}

	@Override
	public void add(int index, E element) {
		ensureSize(size + 1);
		if (index >= 0) System.arraycopy(elements, index, elements, index + 1, size - index);
		elements[index] = element;
	}

	@Override
	public E remove(int index) {
		final E element = elements[index];
		elements[index] = null;
		return element;
	}

	@Override
	public int indexOf(Object o) {
		for (int i = 0, elementsLength = elements.length; i < elementsLength; i++) {
			if (o.equals(elements[i]))
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int lastSeen = -1;
		for (int i = 0, elementsLength = elements.length; i < elementsLength; i++) {
			if (o.equals(elements[i]))
				lastSeen = i;
		}
		return lastSeen;
	}

	@NotNull
	@Override
	public ListIterator<E> listIterator() {
		return createListIterator(0, 0);
	}

	@NotNull
	private ObjectIterators.AbstractIndexBasedListIterator<E> createListIterator(int minPos, int pos) {
		return new ObjectIterators.AbstractIndexBasedListIterator<>(minPos, pos) {
			@Override
			protected void add(int location, E e) {
				UnsafeList.this.add(e);
			}

			@Override
			protected void set(int location, E e) {
				UnsafeList.this.add(location, e);
			}

			@Override
			protected E get(int location) {
				return UnsafeList.this.get(location);
			}

			@Override
			protected void remove(int location) {
				UnsafeList.this.remove(location);
			}

			@Override
			protected int getMaxPos() {
				return elements.length;
			}
		};
	}

	@NotNull
	@Override
	public ListIterator<E> listIterator(int index) {
		return createListIterator(0, index);
	}

	@NotNull
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
}
