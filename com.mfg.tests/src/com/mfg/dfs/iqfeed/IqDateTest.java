package com.mfg.dfs.iqfeed;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

public class IqDateTest {
	
	@SuppressWarnings("static-method")
	@Test
	public void testParseOnlyTime() throws ParseException {
		Date parsed = IqDate.parseOnlyTimeMs("19:03:55.011");
		assertEquals(11,  parsed.getTime() % 1000);
		assertEquals("Fri Jan 02 01:03:55 CET 1970", parsed.toString()); //there are six hours of time zone difference.
		
		GregorianCalendar gc = new GregorianCalendar(1973, 1, 18);
		parsed = IqDate.parseOnlyTimeMs("19:50:22.193", (Calendar) gc.clone());
		assertEquals("Mon Feb 19 01:50:22 CET 1973", parsed.toString()); //I have passed one day!
		
		parsed = IqDate.parseOnlyTimeMs("11:20:41.003", gc);
		assertEquals("Sun Feb 18 17:20:41 CET 1973", parsed.toString());
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void parseMillisecondsDate() throws ParseException{
		Date parsed = IqDate.parseTickDate("2013-07-05 15:10:31.023");
		assertEquals(23, parsed.getTime() % 1000); //this means that I have parsed the milliseconds.
		assertEquals("Fri Jul 05 21:10:31 CEST 2013", parsed.toString());
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testOnlyDate() throws ParseException{
		Date parsed = IqDate.parseIqOnlyDate("2013-07-05");
		
		//we are six our later, so our time is 6.am of the same day.
		assertEquals("Fri Jul 05 06:00:00 CEST 2013", parsed.toString());
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testFormatHistory() throws ParseException {
		Date parsed = IqDate.parseTickDate("2013-07-05 18:00:00.023");
		
		String history = IqDate.formatToHistory(parsed);
		
		assertEquals("20130705 180000", history);
		
		history = IqDate.formatToHistoryDate(parsed);
		assertEquals("20130705", history);
	}

	@SuppressWarnings("static-method")
	@Test
	public void test() throws ParseException {
		Date parsed = IqDate.parseIqDate("2013-07-05 18:00:00");
		
		//we are six our later, so our time is midnight of saturday!
		assertEquals("Sat Jul 06 00:00:00 CEST 2013", parsed.toString());
		
		String utcDate = IqDate.parseIqDateToUtc("2013-07-05 18:00:00");
		assertEquals("05/07/13 22.00", utcDate);
		
		//the date in utc is the same...
//		Date utcDateO = IqDate.parseIqDateToUtcDate("2013-07-05 18:00:00");
//		assertEquals("Sat Jul 06 00:00:00 CEST 2013", utcDateO.toString());
		
//		long checkUtc = utcDateO.getTime();
//		long parsedUtc = parsed.getTime();
//		
//		assertEquals(checkUtc, parsedUtc);
		
	}

}
