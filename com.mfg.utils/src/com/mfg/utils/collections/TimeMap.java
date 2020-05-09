package com.mfg.utils.collections;

import static java.lang.System.out;

import java.util.Arrays;

public class TimeMap {
	private static int CHUNK_SIZE = 1024;
	private long[] _data;
	private int _lastIndex;

	public TimeMap() {
		_data = new long[CHUNK_SIZE];
		_lastIndex = -1;
	}

	public synchronized void put(int index, long time) {
		if (index >= _data.length) {
			_data = Arrays.copyOf(_data, _data.length * 2);
		}
		if (index > _lastIndex) {
			_lastIndex = index;
		}
		_data[index] = time;
	}

	public int size() {
		return _lastIndex + 1;
	}

	public synchronized long get(int index) {
		if (index > _lastIndex) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ (_lastIndex + 1));
		}
		return _data[index];
	}

	public synchronized int indexOf(long time) {
		int low = 0;
		int high = _lastIndex;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			long midVal = _data[mid];

			if (midVal < time)
				low = mid + 1;
			else if (midVal > time)
				high = mid - 1;
			else
				return mid; // key found
		}
		return low == 0 ? 0 : low - 1; // key not found, return floor-index.
	}

	public static void main(String[] args) {
		TimeMap p = new TimeMap();
		long t = System.currentTimeMillis();
		for (int i = 0; i < 1_000_000; i++) {
			p.put(i, i);
		}
		out.println(System.currentTimeMillis() - t + " ms");

		t = System.currentTimeMillis();
		for (int i = 0; i < 1_000_000; i++) {
			p.indexOf(i);
		}
		out.println(System.currentTimeMillis() - t + " ms");
	}
}
