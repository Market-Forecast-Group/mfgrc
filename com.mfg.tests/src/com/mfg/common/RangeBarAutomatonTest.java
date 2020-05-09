package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RangeBarAutomatonTest {

	@SuppressWarnings({ "static-method", "deprecation" })
	@Test
	public void testOneRange() {
		// Let's try to create a range bar automaton and do some tests
		RangeBarAutomaton rba = new RangeBarAutomaton(10, 1);
		Bar bar = rba.acceptNewTick(new Tick(100, 40, 31));

		assertEquals(
				"[Thu Jan 01 01:00:00 CET 1970,40,40,40,40 vol1 31 vol2 0 vtot 31]",
				bar.toString());
		assertEquals(100, bar.getTime());

		bar = rba.getLastCompleteBar();
		assertNull(bar);

		bar = rba.acceptNewTick(new Tick(110, 50, 12));
		assertNull(bar);

		bar = rba.getFormingBar();
		assertEquals(
				"[Thu Jan 01 01:00:00 CET 1970,40,50,40,50 vol1 31 vol2 12 vtot 43]",
				bar.toString());
		assertEquals(110, bar.getTime());

		bar = rba.getLastCompleteBar();
		assertNull(bar);

		// I exit the range
		bar = rba.acceptNewTick(new Tick(4000, 60, 5));

		// this is the new bar
		assertEquals(
				"[Thu Jan 01 01:00:04 CET 1970,60,60,60,60 vol1 5 vol2 0 vtot 5]",
				bar.toString());

		bar = rba.getLastCompleteBar();
		assertEquals(
				"[Thu Jan 01 01:00:00 CET 1970,40,50,40,50 vol1 31 vol2 12 vtot 43]",
				bar.toString());

		// Now I exit the range!
		bar = rba.acceptNewTick(new Tick(5000, 90, 10));

		assertEquals(
				"[Thu Jan 01 01:00:05 CET 1970,90,90,90,90 vol1 10 vol2 0 vtot 10]",
				bar.toString());

		bar = rba.getLastCompleteBar();
		// this bar is the last bar with all the prices equals, because I have
		// exited the range
		assertEquals(
				"[Thu Jan 01 01:00:04 CET 1970,60,60,60,60 vol1 5 vol2 0 vtot 5]",
				bar.toString());

		// Ok, I go down
		bar = rba.acceptNewTick(new Tick(6000, 80, 12));
		assertNull(bar);
		bar = rba.getFormingBar();
		assertEquals(
				"[Thu Jan 01 01:00:06 CET 1970,90,90,80,80 vol1 10 vol2 12 vtot 22]",
				bar.toString());

		bar = rba.getLastCompleteBar();
		assertNull(bar);

		// go up
		bar = rba.acceptNewTick(new Tick(7000, 90, 7));
		assertNull(bar);
		bar = rba.getFormingBar();
		assertEquals(
				"[Thu Jan 01 01:00:07 CET 1970,90,90,80,90 vol1 17 vol2 12 vtot 29]",
				bar.toString());

		bar = rba.getLastCompleteBar();
		assertNull(bar);

		// go down
		bar = rba.acceptNewTick(new Tick(8000, 80, 5));
		assertNull(bar);
		bar = rba.getFormingBar();
		assertEquals(
				"[Thu Jan 01 01:00:08 CET 1970,90,90,80,80 vol1 17 vol2 17 vtot 34]",
				bar.toString());

		bar = rba.getLastCompleteBar();
		assertNull(bar);

		// exit range
		bar = rba.acceptNewTick(new Tick(9000, 70, 3));
		assertEquals(
				"[Thu Jan 01 01:00:09 CET 1970,70,70,70,70 vol1 3 vol2 0 vtot 3]",
				bar.toString());
		bar = rba.getLastCompleteBar();
		assertEquals(
				"[Thu Jan 01 01:00:08 CET 1970,90,90,80,80 vol1 17 vol2 17 vtot 34]",
				bar.toString());

	}

}
