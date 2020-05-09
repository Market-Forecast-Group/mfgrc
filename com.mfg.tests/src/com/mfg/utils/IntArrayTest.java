package com.mfg.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntArrayTest {

	/**
	 * simple test adding and searching inexact inside.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testAdd2() {

		IntArray ia = new IntArray();

		for (int i = 0; i < 10_000; i++) {
			ia.add(i * 5);
		}

		assertTrue(ia.size() == 10_000);

		for (int i = 0; i < 10_000; ++i) {
			assertEquals(i * 5, ia.get(i));
		}

		int val = ia.binarySearch(88);

		// 88 / 5 = 17, in integer division (17.6)
		assertEquals(88 / 5, val);

		assertEquals(85, ia.get(val));

		// floor of below the first
		val = ia.binarySearch(-1);
		assertEquals(-1, val);

		// equal to the max
		val = ia.binarySearch(49_995);
		assertEquals(9999, val);

		// above the max
		val = ia.binarySearch(50_001);
		assertEquals(9999, val);
		assertEquals(49_995, ia.get(9999));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testAdd() {
		IntArray ia = new IntArray();

		for (int i = 0; i < 10_000; i++) {
			ia.add(i);
		}

		assertTrue(ia.size() == 10_000);

		for (int i = 0; i < 10_000; ++i) {
			assertEquals(i, ia.get(i));
		}

		int val = ia.binarySearch(88);

		assertEquals(88, val);

	}

}
