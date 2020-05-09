package com.mfg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectListenersGroup<T> {

	private List<ObjectListener<T>> list = Collections
			.synchronizedList(new ArrayList<ObjectListener<T>>());

	public void handle(T o) {
		for (ObjectListener<T> e : list) {
			e.handle(o);
		}
	}

	public void addObjectListener(ObjectListener<T> listener) {
		list.add(listener);
	}

	public void removeObjectListener(ObjectListener<T> listener) {
		list.remove(listener);
	}
}
