//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.Validate;

@SuppressWarnings("unchecked")
public final class ArrayUT<E> {
	private E[] _array;

	public ArrayUT(final E... elements) {
		this.setArray(elements);
	}

	public E[] getArray() {
		return this._array;
	}

	public void setArray(final E[] array) {
		Validate.notNull(array, "The array must not be null.");
		this._array = array;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(final Object other) {
		return other instanceof ArrayUT && Arrays.equals(this._array, ((ArrayUT) other)._array);
	}

	public static List<String> listFromString(final String a) {
		return listFromString(a, "<@>");
	}

	public static List<String> listFromString(final String a, final String splitter) {
		if (a == null || a.isEmpty()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(Arrays.asList(a.split(splitter)));
	}

	public static String stringFromList(final List<String> a) {
		return stringFromList(a, "<@>");
	}

	public static String stringFromList(final List<String> a, final String splitter) {
		if (a == null || a.isEmpty()) {
			return "";
		}
		String result = "";
		for (final String m : a) {
			result = String.valueOf(result) + m + splitter;
		}
		if (result.endsWith(splitter)) {
			result.substring(0, result.length() - splitter.length());
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this._array);
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public static <T> T[] toArray(final Iterable<? extends T> list, final Class<T> c) {
		int size = -1;
		if (list instanceof Collection) {
			final Collection coll = (Collection) list;
			size = coll.size();
		}
		if (size < 0) {
			size = 0;
			for (final T element : list) {
				++size;
			}
		}
		final Object[] result = (Object[]) Array.newInstance(c, size);
		int i = 0;
		for (final T element2 : list) {
			result[i++] = element2;
		}
		return (T[]) result;
	}
}
