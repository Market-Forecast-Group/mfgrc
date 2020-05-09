package com.mfg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;

public class ListenerSupport {
	private List<Runnable> list = Collections
			.synchronizedList(new ArrayList<Runnable>());

	public void addListener(Runnable r) {
		list.add(r);
	}

	public void addListener(final Action action) {
		addListener(new Runnable() {

			@Override
			public void run() {
				action.run();
			}
		});
	}

	public void removeListener(Runnable r) {
		list.remove(r);
	}

	public void fire() {
		synchronized (list) {
			for (Runnable r : list) {
				r.run();
			}
		}
	}

}
