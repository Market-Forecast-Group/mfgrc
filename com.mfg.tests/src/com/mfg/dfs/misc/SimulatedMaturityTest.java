package com.mfg.dfs.misc;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.mfg.common.BarType;
import com.mfg.common.Maturity;

public class SimulatedMaturityTest {

	@SuppressWarnings("static-method")
	@Test
	public void testGetStartDataTime() {
		// a Maturity in the past.

		Calendar gc = Calendar.getInstance();
		gc.clear();
		gc.set(1990, 0, 20); // January 20th 1990

		Maturity mat = new Maturity(gc);

		SimulatedMaturity sm = new SimulatedMaturity(mat);

		Date dailyDate = sm.getStartDataTime(BarType.DAILY);
		assertEquals("Tue Dec 20 00:00:00 CET 1988", dailyDate.toString());

	}

}
