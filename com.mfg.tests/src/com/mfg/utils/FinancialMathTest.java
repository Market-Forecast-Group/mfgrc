package com.mfg.utils;

import static com.mfg.utils.FinancialMath.getExactDeltaTicks;
import static com.mfg.utils.FinancialMath.stringPriceToInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mfg.common.DFSException;

public class FinancialMathTest {

	@SuppressWarnings({ "static-method", "boxing" })
	@Test
	public void testExactDelta() {

		int val = getExactDeltaTicks(10, 20, 10);
		assertEquals(1, val);

		val = getExactDeltaTicks(20, 10, 10);
		assertEquals(1, val);

		val = getExactDeltaTicks(10, 100, 5);
		assertEquals(18, val);

		boolean ok = false;
		try {
			getExactDeltaTicks(10, 20, 11);
		} catch (IllegalArgumentException e) {
			ok = true;
		}

		assertEquals(true, ok);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testStringPriceToInt() throws DFSException {
		int val = stringPriceToInt("192.33", 2);
		assertEquals(19233, val);
		boolean ok = false;
		try {
			val = stringPriceToInt("19838.991", 2);
		} catch (DFSException e) {
			ok = true;
		}
		assertTrue(ok); // I must round it

		val = stringPriceToInt("19838.991", 3);
		assertEquals(19838991, val);

		ok = false;
		try {
			val = stringPriceToInt("-1", 0);
		} catch (IllegalArgumentException e) {
			ok = true;
		}

		assertTrue(ok);

		val = stringPriceToInt("999999.999", 3);
		// this is the maximum val
		assertEquals(999999999, val);

		ok = false;
		try {
			val = stringPriceToInt("123.4567891", 7);
		} catch (IllegalArgumentException e) {
			ok = true;
		}

		assertTrue(ok);

		val = stringPriceToInt("123.456789", 6);
		assertEquals(123456789, val);
	}

}
