package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mfg.utils.Yadc;

public class TimeBarIntegratorTest {

	@SuppressWarnings("static-method")
	@Test
	public void testCompleteBar() {
		TimeBarIntegrator tbi = new TimeBarIntegrator(10, 5, false);

		Bar aBar = new Bar(10, 10, 14, 5, 12, 100);
		Bar formingBar = tbi.acceptBar(aBar);
		assertNotNull(formingBar);
		assertEquals("[Thu Jan 01 01:00:00 CET 1970,10,14,5,12 vol 100]",
				formingBar.toString());
		assertNull(tbi.getLastCompleBar());

		aBar = new Bar(20, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		assertNull(tbi.getLastCompleBar());

		aBar = new Bar(30, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		assertNull(tbi.getLastCompleBar());

		// aBar = new Bar(40, 10, 14, 5, 12, 100);
		// formingBar = tbi.acceptBar(aBar);
		// assertNull(formingBar);
		// assertNull(tbi.getLastCompleBar());

		aBar = new Bar(50, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		Bar completeBar = tbi.getLastCompleBar();
		assertNotNull(completeBar);

		assertEquals("[Thu Jan 01 01:00:00 CET 1970,10,14,5,12 vol 400]",
				completeBar.toString());
		assertFalse(tbi.isLastCompletedBarFull());

		aBar = new Bar(60, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNotNull(formingBar);
		assertEquals("[Thu Jan 01 01:00:00 CET 1970,10,14,5,12 vol 100]",
				formingBar.toString());
		assertNull(tbi.getLastCompleBar());

		aBar = new Bar(70, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		assertNull(tbi.getLastCompleBar());

		aBar = new Bar(80, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		assertNull(tbi.getLastCompleBar());

		aBar = new Bar(90, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		assertNull(tbi.getLastCompleBar());

		aBar = new Bar(100, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);

		completeBar = tbi.getLastCompleBar();
		assertNotNull(completeBar);

		assertEquals("[Thu Jan 01 01:00:00 CET 1970,10,14,5,12 vol 500]",
				completeBar.toString());
		assertTrue(tbi.isLastCompletedBarFull());

	}

	@SuppressWarnings("static-method")
	@Test
	public void testAcceptBar() {
		// I create a new tbi of 5 minutes.
		TimeBarIntegrator tbi = new TimeBarIntegrator(60_000, 5, false);

		Bar aBar = new Bar(60_000, 10, 14, 5, 12, 100);

		Bar formingBar = tbi.acceptBar(aBar);
		assertNotNull(formingBar);
		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,14,5,12 vol 100]",
				formingBar.toString());
		Bar completeBar = tbi.getLastCompleBar();
		assertNull(completeBar);

		aBar = new Bar(120_000, 17, 22, 16, 20, 97);
		formingBar = tbi.acceptBar(aBar);

		assertNull(formingBar);
		formingBar = tbi.getFormingBar();
		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,22,5,20 vol 197]",
				formingBar.toString());

		completeBar = tbi.getLastCompleBar();
		assertNull(completeBar);

		// this is the last bar of the period
		aBar = new Bar(300_000, 4, 5, 1, 3, 200);
		formingBar = tbi.acceptBar(aBar);

		assertNull(formingBar); // the new period is BLANK
		// assertEquals("[Thu Jan 01 01:10:00 CET 1970,3,3,3,3 vol 0]",
		// formingBar.toString());

		completeBar = tbi.getLastCompleBar();
		assertNotNull(completeBar);

		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,22,1,3 vol 397]",
				completeBar.toString());

		assertFalse(tbi.isLastCompletedBarFull());

		// now I give the 1st bar of the new period.
		aBar = new Bar(360_000, 100, 130, 90, 105, 1000);
		formingBar = tbi.acceptBar(aBar);

		assertNotNull(formingBar);
		// formingBar = tbi.getFormingBar();
		assertEquals("[Thu Jan 01 01:10:00 CET 1970,100,130,90,105 vol 1000]",
				formingBar.toString());

		completeBar = tbi.getLastCompleBar();
		assertNull(completeBar);

		// assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,22,1,3 vol 397]",
		// completeBar.toString());

		// Ok, now I go completely with a new time period

		aBar = new Bar(660_000, 88, 120, 50, 65, 999);
		formingBar = tbi.acceptBar(aBar);

		assertNotNull(formingBar);
		assertEquals("[Thu Jan 01 01:15:00 CET 1970,88,120,50,65 vol 999]",
				formingBar.toString());

		completeBar = tbi.getLastCompleBar();
		assertNotNull(completeBar);

		assertFalse(tbi.isLastCompletedBarFull());

		assertEquals("[Thu Jan 01 01:10:00 CET 1970,100,130,90,105 vol 1000]",
				completeBar.toString());
	}

	@Test
	@SuppressWarnings("static-method")
	public void testStartingIntegrator() {
		TimeBarIntegrator tbi = new TimeBarIntegrator(60_000, 5, true);

		/*
		 * this is the STARTING bar of the next period, because I use the
		 * starting times.
		 */
		Bar aBar = new Bar(300_000, 10, 14, 5, 12, 100);

		Bar formingBar = tbi.acceptBar(aBar);
		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,14,5,12 vol 100]",
				formingBar.toString());
		Bar completeBar = tbi.getLastCompleBar();
		assertNull(completeBar);

		tbi = new TimeBarIntegrator(60_000, 5, true);

		/*
		 * this is the ENDING BAR for the first period.
		 */
		aBar = new Bar(240_000, 10, 14, 5, 12, 100);
		formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		completeBar = tbi.getLastCompleBar();
		assertEquals("[Thu Jan 01 01:00:00 CET 1970,10,14,5,12 vol 100]",
				completeBar.toString());
	}

	@Test
	@SuppressWarnings("static-method")
	public void testAcceptUnconventionalBars1() {
		TimeBarIntegrator tbi = new TimeBarIntegrator(60_000, 5, false);

		Bar aBar = new Bar(300_000, 10, 14, 5, 12, 100);

		Bar formingBar = tbi.acceptBar(aBar);
		assertNull(formingBar);
		// assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,14,5,12 vol 100]",
		// formingBar.toString());

		Bar completeBar = tbi.getLastCompleBar();
		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,14,5,12 vol 100]",
				completeBar.toString());
	}

	@Test
	@SuppressWarnings("static-method")
	public void testAcceptUnconventionalBars() {
		// I create a new tbi of 5 minutes.
		TimeBarIntegrator tbi = new TimeBarIntegrator(60_000, 5, false);

		Bar aBar = new Bar(60_012, 10, 14, 5, 12, 100);

		Bar formingBar = tbi.acceptBar(aBar);
		assertNotNull(formingBar);
		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,14,5,12 vol 100]",
				formingBar.toString());
		Bar completeBar = tbi.getLastCompleBar();
		assertNull(completeBar);

		aBar = new Bar(120_000, 500, 700, 300, 600, 3);
		formingBar = tbi.acceptBar(aBar);

		/*
		 * This is the end, because the first bar dictates the offset.
		 */
		aBar = new Bar(300_012, 5, 7, 3, 6, 5);
		formingBar = tbi.acceptBar(aBar);

		completeBar = tbi.getLastCompleBar();
		assertEquals("[Thu Jan 01 01:05:00 CET 1970,10,700,3,6 vol 108]",
				completeBar.toString());
		assertEquals(completeBar.time, 300_012);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testBugInMultipleDays() {
		//
		TimeBarIntegrator tbi = new TimeBarIntegrator(Yadc.ONE_DAY_MSEC, 2,
				true);

		/*
		 * These are the bar to test
		 * 
		 * 0 [Thu Dec 01 06:00:00 CET 2011,182744,182744,182744,182744 vol 0]
		 * 
		 * 1 [Fri Dec 02 06:00:00 CET 2011,183994,183994,183994,183994 vol 0]
		 * 
		 * 2 [Mon Dec 05 06:00:00 CET 2011,182244,182244,182244,182244 vol 0]
		 * 
		 * 3 [Tue Dec 06 06:00:00 CET 2011,181919,181919,181919,181919 vol 0]
		 * 
		 * 4 [Wed Dec 07 06:00:00 CET 2011,183144,183144,183144,183144 vol 0]
		 * 
		 * 5 [Thu Dec 08 06:00:00 CET 2011,179894,179894,179894,179894 vol 0]
		 */

		Bar aBar;
		Bar completeBar;
		// 0 [Thu Dec 01 06:00:00 CET 2011,182744,182744,182744,182744 vol 0]
		aBar = new Bar(1322715600000L, 182744, 182744, 182744, 182744, 0);
		Bar formingBar = tbi.acceptBar(aBar);
		completeBar = tbi.getLastCompleBar();
		assertNull(formingBar);
		/*
		 * The complete bar has the starting time set, so is the bar which
		 * includes Nov. 30th AND Dec 1st. [30/1]
		 */
		assertEquals(
				"[Wed Nov 30 06:00:00 CET 2011,182744,182744,182744,182744 vol 0]",
				completeBar.toString());

		/*
		 * The next complete bar should have the time Dec 2nd and will include
		 * Dec 2nd AND Dec 3rd. [2/3]
		 * 
		 * But, as Dec 3rd is Saturday the integrator should return this bar
		 * when it receives the next bar, which is december 5th
		 */

		// 1 [Fri Dec 02 06:00:00 CET 2011,183994,183994,183994,183994 vol 0]
		aBar = new Bar(1322802000000L, 183994, 183994, 183994, 183994, 0);
		formingBar = tbi.acceptBar(aBar);
		completeBar = tbi.getLastCompleBar();

		assertEquals(formingBar.toString(),
				"[Fri Dec 02 06:00:00 CET 2011,183994,183994,183994,183994 vol 0]");
		assertNull(completeBar);

		/*
		 * Here it is a strange case, because when the Dec 5th bar is received I
		 * know that the Dec 2nd bar is finished, so the complete bar is OK.
		 * 
		 * The forming bar has a time of Sunday 4th and should include Sunday
		 * 4th and Monday 5th, but this is already here, so I have two complete
		 * bars..., even if the second will be notified after.
		 * 
		 * the next complete bar is [4/5]
		 */

		// 2 [Mon Dec 05 06:00:00 CET 2011,182244,182244,182244,182244 vol 0]
		aBar = new Bar(1323061200000L, 182244, 182244, 182244, 182244, 0);
		formingBar = tbi.acceptBar(aBar);
		completeBar = tbi.getLastCompleBar();

		assertEquals(
				"[Sun Dec 04 06:00:00 CET 2011,182244,182244,182244,182244 vol 0]",
				formingBar.toString());
		assertEquals(
				"[Fri Dec 02 06:00:00 CET 2011,183994,183994,183994,183994 vol 0]",
				completeBar.toString());

		/*
		 * Now, when the integrator receives the Dec 6th bar it means that the
		 * Dec 4th has finished, and the 6th has begun
		 */

		// 3 [Tue Dec 06 06:00:00 CET 2011,181919,181919,181919,181919 vol 0]
		aBar = new Bar(1323147600000L, 181919, 181919, 181919, 181919, 0);
		formingBar = tbi.acceptBar(aBar);
		completeBar = tbi.getLastCompleBar();

		assertEquals(
				"[Tue Dec 06 06:00:00 CET 2011,181919,181919,181919,181919 vol 0]",
				formingBar.toString());
		assertEquals(
				"[Sun Dec 04 06:00:00 CET 2011,182244,182244,182244,182244 vol 0]",
				completeBar.toString());

		/*
		 * The dec. 7th bar is inside the period, so the complete bar is not set
		 * and the forming bar either
		 */
		// 4 [Wed Dec 07 06:00:00 CET 2011,183144,183144,183144,183144 vol 0]
		aBar = new Bar(1323234000000L, 183144, 183144, 183144, 183144, 0);
		formingBar = tbi.acceptBar(aBar);
		completeBar = tbi.getLastCompleBar();

		assertNull(formingBar);
		assertNull(completeBar);

		/*
		 * The dec. 8th bar starts the new period and ends the preceiding.
		 */
		// 5 [Thu Dec 08 06:00:00 CET 2011,179894,179894,179894,179894 vol 0]
		aBar = new Bar(1323320400000L, 179894, 179894, 179894, 179894, 0);
		formingBar = tbi.acceptBar(aBar);
		completeBar = tbi.getLastCompleBar();

		assertEquals(
				"[Thu Dec 08 06:00:00 CET 2011,179894,179894,179894,179894 vol 0]",
				formingBar.toString());
		/*
		 * true because the open is equal to dec.6th and close equal to dec. 7th
		 */
		assertEquals(
				"[Tue Dec 06 06:00:00 CET 2011,181919,183144,181919,183144 vol 0]",
				completeBar.toString());

	}
}
