package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RequestParamsTest {

	@SuppressWarnings("static-method")
	@Test
	public void testSerialize() throws DFSException {
		RequestParams par = RequestParams.createAllBarsRequestDaily("symb");
		// assertEquals("symb,ALL_BARS,DAILY,1,0,19700101 010000,19700101 010000",
		// par.serialize());

		// String proxyId = par.getReqId();

		// I have to make sure to match the random id with a regular expression,
		// just to have a good test.
		assertTrue(par
				.serialize()
				.matches(
						"symb,ALL_BARS,DAILY,1,0,1970-01-01 01:00:00.000,1970-01-01 01:00:00.000"));

		// RequestParams stubPar = RequestParams.parse(par.serialize());

		// assertEquals(proxyId, stubPar.getReqId());

		RequestParams checkPar = RequestParams
				.parse("symb,ALL_BARS,DAILY,1,0,1970-01-01 01:00:01.000,1970-01-01 01:00:02.000");
		assertEquals("symb", checkPar.getSymbol());
		assertEquals(BarType.DAILY, checkPar.getBarType());
		assertEquals(1000, checkPar.getStartTime()); // one second after
														// midnight UTC
		assertEquals(2000, checkPar.getEndTime()); // two seconds after midnight
													// UTC.

		checkPar = RequestParams
				.parse("symb,NUM_BARS,MINUTE,5,100,1970-01-01 01:00:01.000,1970-01-01 01:00:02.000");
		assertEquals("symb", checkPar.getSymbol());
		assertEquals(BarType.MINUTE, checkPar.getBarType());
		assertEquals(1000, checkPar.getStartTime()); // one second after
														// midnight UTC
		assertEquals(2000, checkPar.getEndTime()); // two seconds after midnight
													// UTC.
		assertEquals(5, checkPar.getBarWidth());
		assertEquals(100, checkPar.getNumBarsOrDays());
	}

}
