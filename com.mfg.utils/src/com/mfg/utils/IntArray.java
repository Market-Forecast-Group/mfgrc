package com.mfg.utils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A resizable array of integers.
 * 
 * <p>
 * There are many other implementations, but they are really fat, I wanted a
 * simple class to be used in this project so I write my own.
 * 
 * <p>
 * The requirements are fairly easy, just create the array, store some int
 * values there and grow, if necessary.
 * 
 * @author Sergio
 * 
 */
public class IntArray implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4271195336014989368L;

	private static final int INITIAL_CAPACITY = 10;

	/**
	 * This is the array which holds the values, the capacity is simply the
	 * array length.
	 * <p>
	 * The array for now will ever grow not shrink
	 */
	private int[] _data;

	/**
	 * the current size of the array.
	 * 
	 * <p>
	 * _pos is always less then (or equal) to the lenght of the real array.
	 */
	private int _pos;

	public IntArray() {
		this(INITIAL_CAPACITY);
	}

	/**
	 * Creates a new <code>TIntArrayList</code> instance with the specified
	 * capacity.
	 * 
	 * @param capacity
	 *            an <code>int</code> value
	 */
	public IntArray(int capacity) {
		_data = new int[capacity];
		_pos = 0;
	}

	/**
	 * gets the value at the given index. May return an exception if the index
	 * is out of bounds.
	 * 
	 * @param index
	 *            the index, must be between 0 and size -1;
	 * @return the value at the given index
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the index is out of bounds.
	 */
	public int get(int index) {
		if (index >= _pos) {
			throw new ArrayIndexOutOfBoundsException("index " + index
					+ " size " + _pos);
		}
		return _data[index];
	}

	public int size() {
		return _pos;
	}

	public boolean add(int val) {
		ensureCapacity(_pos + 1);
		_data[_pos++] = val;
		return true;
	}

	/**
	 * Grow the internal array as needed to accommodate the specified number of
	 * elements. The size of the array bytes on each resize unless capacity
	 * requires more than twice the current capacity.
	 */
	public void ensureCapacity(int capacity) {
		if (capacity > _data.length) {
			int newCap = Math.max(_data.length << 1, capacity);
			int[] tmp = new int[newCap];
			System.arraycopy(_data, 0, tmp, 0, _data.length);
			_data = tmp;
		}
	}

	/**
	 * This is the "pure" binary search inside the array.
	 * <p>
	 * Its behavior is identical to {@link Arrays#binarySearch(int[], int)}
	 * 
	 * @param key
	 *            the key to find
	 * @return @see {@link Arrays#binarySearch(int[], int)}
	 */
	public int binarySearchPure(int key) {
		int res = Arrays.binarySearch(_data, 0, _pos, key);
		return res;
	}

	/**
	 * performs a binary search on the arrays elements.
	 * <p>
	 * Of course the data must be in order.
	 * 
	 * <p>
	 * If the key is not found it will return index of the key which is the
	 * highest of the keys which are less than this one, that is the index of
	 * the "floor" of this key, except if the
	 * 
	 * <p>
	 * Special cases.
	 * 
	 * <p>
	 * Key equal to the first pos, return 0
	 * 
	 * <p>
	 * Key below the first, return -1
	 * 
	 * <p>
	 * Key equal to the maximum key, return {@link #size()} -1
	 * 
	 * <p>
	 * Key above the maximum key, return {@link Integer#MAX_VALUE}.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public int binarySearch(int key) {
		int res = Arrays.binarySearch(_data, 0, _pos, key);

		// /*
		// * if the key is above, please return max value
		// */
		// if (res == (_pos + 1) * -1) {
		// return Integer.MAX_VALUE;
		// }

		if (res < 0) {
			res = -res - 2;
		}

		return res;
	}

}
