package com.patech.utils;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtils {
	/*
	 * finds the nth element from the given list
	 */
	public static <T> T findElement(Collection<T> list, int n) {
		int counter = 0;
		if (list == null)
			return null;
		Iterator<T> iterator = list.iterator();
		while (iterator.hasNext() && counter++ < n) {
			iterator.next();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}
}
