package com.mfg.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

public class YadcTest {

	@SuppressWarnings("static-method")
	@Test
	public void testIsOneDaySince() {
		Calendar cal = new GregorianCalendar(1999, 11, 31, 23, 50, 50); // we
																		// are
																		// approaching
																		// the
																		// millenium
		assertEquals("Fri Dec 31 23:50:50 CET 1999", cal.getTime().toString());

		Calendar afterOneHour = new GregorianCalendar(2000, 0, 1, 0, 50, 50); // we
																				// are
																				// approaching
																				// the
																				// millenium
		assertEquals("Sat Jan 01 00:50:50 CET 2000", afterOneHour.getTime()
				.toString());

		assertFalse(Yadc.isADayBetween(cal.getTimeInMillis(),
				afterOneHour.getTimeInMillis()));

		// Not a complete day has passed, but we have passed one calendar day.
		assertTrue(Yadc.isOneCalendarDayPassed(cal, afterOneHour));

	}

	@SuppressWarnings("static-method")
	@Test
	public void testDiffDays() {
		/* Test the difference in days between two dates. */

		Calendar cal = new GregorianCalendar(1999, 11, 31);
		Calendar cal1st = new GregorianCalendar(2000, 0, 1);

		long diff = Yadc.daysDiff(cal.getTimeInMillis(),
				cal1st.getTimeInMillis());

		assertEquals(1, diff);

		// backwards
		diff = Yadc.daysDiff(cal1st.getTimeInMillis(), cal.getTimeInMillis());

		assertEquals(-1, diff);
	}

}
