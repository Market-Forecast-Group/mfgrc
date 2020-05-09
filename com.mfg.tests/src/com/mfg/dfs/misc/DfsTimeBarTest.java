package com.mfg.dfs.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.UnparsedBar;

public class DfsTimeBarTest {

	@SuppressWarnings({ "static-method", "unused" })
	@Test
	public void testTick6() {
		// I would like to test with a wrong tick
		Bar bar = new Bar(11, 10, 40, 5, 25, 3289);

		// DfsTimeBar dtb;
		boolean goodCatch = false;

		try {
			/* dtb = */new DfsTimeBar(bar, 7);
		} catch (ArithmeticException e) {
			goodCatch = true;
		}

		assertTrue(goodCatch);
		goodCatch = false;

		// incoherent bar, high < low
		bar = new Bar(11, 20, 5, 15, 25, 3289);
		try {
			/* dtb = */new DfsTimeBar(bar, 7);
		} catch (ArithmeticException e) {
			goodCatch = true;
		}
		assertTrue(goodCatch);
		goodCatch = false;
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTick5() {
		// I would like to test with a tick size of 5
		Bar bar = new Bar(11, 10, 40, 5, 25, 3289);

		DfsTimeBar dtb = new DfsTimeBar(bar, 5);

		assertEquals(11, dtb.timestamp);
		assertEquals(5, dtb.low);
		assertEquals((10 - 5) / 5, dtb.to_open);
		assertEquals((40 - 5) / 5, dtb.to_high);
		assertEquals((25 - 5) / 5, dtb.to_close);

		Bar toBar = dtb.decodeTo(5);

		assertEquals(5, toBar.getLow());
		assertEquals(10, toBar.getOpen());
		assertEquals(40, toBar.getHigh());
		assertEquals(25, toBar.getClose());
		assertEquals(3289, toBar.getVolume());
	}

	@SuppressWarnings("static-method")
	@Test
	public void test() {

		Bar bar = new Bar(11, 12, 90, 1, 33, 5464);

		DfsTimeBar dtb = new DfsTimeBar(bar, 1);
		DfsTimeBar dtb1 = new DfsTimeBar(bar, 1);

		assertTrue(dtb.equals(dtb1));
		assertTrue(dtb.hashCode() == dtb1.hashCode()); // just a simple test.

		assertEquals(11, dtb.timestamp);
		assertEquals(1, dtb.low);
		assertEquals(12 - 1, dtb.to_open);
		assertEquals(90 - 1, dtb.to_high);
		assertEquals(33 - 1, dtb.to_close);

		dtb1.timestamp++;
		assertFalse(dtb.equals(dtb1));
		assertFalse(dtb.hashCode() == dtb1.hashCode());

		// Ok, Now I test the reverse:

		Bar toBar = dtb.decodeTo(1);

		assertEquals(1, toBar.getLow());
		assertEquals(12, toBar.getOpen());
		assertEquals(90, toBar.getHigh());
		assertEquals(33, toBar.getClose());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTimeBarString() throws DFSException {
		UnparsedBar ub = new UnparsedBar(829, "11.01", "11.99", "10.33",
				"11.88", 8292);
		DfsTimeBar dtb = new DfsTimeBar(ub, 2, 1, false);

		Bar decoded = dtb.decodeTo(1);
		Bar reference = new Bar(829, 1101, 1199, 1033, 1188, 8292);

		assertTrue(decoded.equals(reference));

	}

	@SuppressWarnings("static-method")
	@Test
	public void testEqual() {
		Bar bar = new Bar(1000, 10, 90, 10, 50, 563);
		Bar barV = new Bar(1000, 10, 90, 10, 50, 564);
		DfsTimeBar dtb = new DfsTimeBar(bar, 10);
		DfsTimeBar dtbV = new DfsTimeBar(barV, 10);

		assertFalse(dtb.equals(dtbV));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testLenient() throws DFSException {
		UnparsedBar ub = new UnparsedBar(829, "11.01", "11.99", "10.33",
				"10.30", 8292);

		boolean goodCatch = false;
		try {
			@SuppressWarnings("unused")
			DfsTimeBar dtb = new DfsTimeBar(ub, 2, 1, false);
		} catch (IllegalArgumentException e) {
			goodCatch = true;
		}
		assertTrue(goodCatch);

		DfsTimeBar dtb = new DfsTimeBar(ub, 2, 1, true);

		Bar decoded = dtb.decodeTo(1);
		Bar reference = new Bar(829, 1101, 1199, 1030, 1030, 8292);

		assertEquals(reference.toString(), decoded.toString());

		// Ok, now with a close greater than the high

		ub = new UnparsedBar(463, "11.01", "11.99", "10.33", "13.30", 8292);

		goodCatch = false;
		try {
			dtb = new DfsTimeBar(ub, 2, 1, false);
		} catch (IllegalArgumentException e) {
			goodCatch = true;
		}
		assertTrue(goodCatch);

		dtb = new DfsTimeBar(ub, 2, 1, true);

		decoded = dtb.decodeTo(1);
		reference = new Bar(829, 1101, 1330, 1033, 1330, 8292);

		assertEquals(reference.toString(), decoded.toString());
	}

}
