package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class BarAutomatonTest {

	@SuppressWarnings("static-method")
	@Test
	public void testWithInitialTime() {
		/*
		 * I want a time bar automaton which creates bar with the initial time
		 * of the period, not the end.
		 */
		TimeBarAutomaton ba = new TimeBarAutomaton(1000, false);

		Bar bar = ba.accept(new Tick(1100, 66, 11));

		assertEquals(1000, bar.getTime()); // the start time is normalized.
		assertEquals("[Thu Jan 01 01:00:01 CET 1970,66,66,66,66 vol 11]",
				bar.toString());

		bar = ba.accept(new Tick(1111, 39, 5));
		assertEquals(null, bar);
		assertEquals(null, ba.getLastCompleteBar());

		bar = ba.getFormingBar();
		assertEquals("[Thu Jan 01 01:00:01 CET 1970,66,66,39,39 vol 16]",
				bar.toString());

		bar = ba.accept(new Tick(1998, 88, 3));
		assertEquals(null, bar);
		assertEquals(null, ba.getLastCompleteBar());

		bar = ba.getFormingBar();
		assertEquals("[Thu Jan 01 01:00:01 CET 1970,66,88,39,88 vol 19]",
				bar.toString());

		bar = ba.accept(new Tick(2000, 4, 1000)); // a sudden crash at the end
													// of the
		// period.

		assertEquals("[Thu Jan 01 01:00:02 CET 1970,4,4,4,4 vol 1000]",
				bar.toString());
		assertEquals("[Thu Jan 01 01:00:01 CET 1970,66,88,39,88 vol 19]", ba
				.getLastCompleteBar().toString());

		bar = ba.getFormingBar();
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,4,4,4,4 vol 1000]",
				bar.toString());

		bar = ba.accept(new Tick(2001, 139, 11));
		assertNull(bar);
		// assertEquals("[Thu Jan 01 01:00:02 CET 1970,139,139,139,139 vol 0]",
		// bar.toString());
		assertNull(ba.getLastCompleteBar());
		// assertEquals("[Thu Jan 01 01:00:02 CET 1970,66,88,4,4 vol 0]", ba
		// .getLastCompleteBar().toString());

		bar = ba.accept(new Tick(3001, 33, 99));
		assertEquals("[Thu Jan 01 01:00:03 CET 1970,33,33,33,33 vol 99]",
				bar.toString());
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,4,139,4,139 vol 1011]", ba
				.getLastCompleteBar().toString());

		bar = ba.accept(new Tick(3003, 99, 11));
		assertEquals(null, bar);
		assertEquals("[Thu Jan 01 01:00:03 CET 1970,33,99,33,99 vol 110]", ba
				.getFormingBar().toString());
		assertEquals(null, ba.getLastCompleteBar());
	}

	@SuppressWarnings("static-method")
	@Test
	public void test() {
		TimeBarAutomaton ba = new TimeBarAutomaton(1000, true);

		Bar bar = ba.accept(new Tick(1100, 66, 19));

		assertEquals(2000, bar.getTime()); // the start time is normalized.
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,66,66,66,66 vol 19]",
				bar.toString());

		bar = ba.accept(new Tick(1111, 39, 24));
		assertEquals(null, bar);
		assertEquals(null, ba.getLastCompleteBar());

		bar = ba.getFormingBar();
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,66,66,39,39 vol 43]",
				bar.toString());

		bar = ba.accept(new Tick(1998, 88, 12));
		assertEquals(null, bar);
		assertEquals(null, ba.getLastCompleteBar());

		bar = ba.getFormingBar();
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,66,88,39,88 vol 55]",
				bar.toString());

		bar = ba.accept(new Tick(2000, 4, 11)); // a sudden crash at the end of
												// the
		// period.
		assertEquals(null, bar);
		assertEquals(null, ba.getLastCompleteBar());

		bar = ba.getFormingBar();
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,66,88,4,4 vol 66]",
				bar.toString());

		bar = ba.accept(new Tick(2001, 139, 12));
		assertEquals("[Thu Jan 01 01:00:03 CET 1970,139,139,139,139 vol 12]",
				bar.toString());
		assertEquals("[Thu Jan 01 01:00:02 CET 1970,66,88,4,4 vol 66]", ba
				.getLastCompleteBar().toString());

		bar = ba.accept(new Tick(3001, 33, 31));
		assertEquals("[Thu Jan 01 01:00:04 CET 1970,33,33,33,33 vol 31]",
				bar.toString());
		assertEquals("[Thu Jan 01 01:00:03 CET 1970,139,139,139,139 vol 12]",
				ba.getLastCompleteBar().toString());

		bar = ba.accept(new Tick(3003, 99, 51));
		assertEquals(null, bar);
		assertEquals("[Thu Jan 01 01:00:04 CET 1970,33,99,33,99 vol 82]", ba
				.getFormingBar().toString());
		assertEquals(null, ba.getLastCompleteBar());
	}

}
