package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import org.junit.Test;

//import com.mfg.dfs.conn.DFSException;

public class MaturityTest {

	@SuppressWarnings("static-method")
	@Test
	public void testStartDate() {
		Calendar gc = Calendar.getInstance();
		gc.clear();
		gc.set(1990, 0, 20); // January 20th 1990

		Maturity mat = new Maturity(gc);

		Date start = mat.getStartTradingData();

		assertEquals("Tue Mar 20 00:00:00 CET 1990", mat.getExpirationDate()
				.toString());
		assertEquals("Tue Dec 20 00:00:00 CET 1988", start.toString());

		assertTrue(mat.isAQuarterMaturity());

	}

	@SuppressWarnings("static-method")
	@Test
	public void testParseMaturity() throws Exception {
		Maturity.ParseMaturityAns pma = Maturity.parseMaturity("helloU34");
		assertEquals(2034, pma.parsedMaturity.getYear());
		assertEquals(2, pma.parsedMaturity.getQuarter());
		assertEquals("hello", pma.unparsedString);
		assertEquals("2034-3Q", pma.parsedMaturity.toFileString());

		boolean ok = false;

		try {
			pma = Maturity.parseMaturity("sfI99");
		} catch (Exception e) {
			ok = true;
		}

		assertTrue(ok);

		pma = Maturity.parseMaturity("dfsa#mfg");

		assertNull(pma.parsedMaturity);
		assertEquals("dfsa", pma.unparsedString);

		pma = Maturity.parseMaturity("@ESM97");
		assertEquals(1997, pma.parsedMaturity.getYear());
		assertEquals(1, pma.parsedMaturity.getQuarter());

		pma = Maturity.parseMaturity("@ESH00");
		assertEquals(2000, pma.parsedMaturity.getYear());
		assertEquals(0, pma.parsedMaturity.getQuarter());
	}

	// @SuppressWarnings("static-method")
	// @Test
	// public void testAddMaturity() {
	// GregorianCalendar gc = new GregorianCalendar();
	// gc.clear();
	// gc.set(2013, 11, 6); // Dec.
	// Maturity mat = new Maturity(gc);
	//
	// assertEquals(mat.add(1), mat.getNext());
	//
	// assertEquals(mat.add(-1), mat.getPrevious());
	//
	// }

	@SuppressWarnings("static-method")
	@Test
	public void testMaturityExpiration() {
		/* some tests */
		GregorianCalendar gc = new GregorianCalendar();
		gc.clear();
		gc.set(1990, 0, 20); // January 20th 1990

		Maturity mat = new Maturity(gc);

		assertEquals("Tue Mar 20 00:00:00 CET 1990", mat.getExpirationDate()
				.toString());

		mat = mat.getNextAsQuarter();

		assertEquals("Wed Jun 20 00:00:00 CEST 1990", mat.getExpirationDate()
				.toString());

	}

	@SuppressWarnings("static-method")
	@Test
	public void testMaturityNextPrev() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.clear();
		gc.set(1990, 0, 20); // January 20th 1990

		Maturity mat = new Maturity(gc);
		assertEquals("1990-1Q", mat.toFileString());

		assertEquals("Sat Dec 16 00:00:00 CET 1989", mat.getStartDate()
				.toString());

		// Ok, now I go next
		Maturity tMat = mat;

		tMat = tMat.getNextAsQuarter();
		assertEquals("1990-2Q", tMat.toFileString());
		assertEquals("Fri Mar 16 00:00:00 CET 1990", tMat.getStartDate()
				.toString());

		tMat = tMat.getNextAsQuarter();
		assertEquals("1990-3Q", tMat.toFileString());
		assertEquals("Sat Jun 16 00:00:00 CEST 1990", tMat.getStartDate()
				.toString());

		tMat = tMat.getNextAsQuarter();
		assertEquals("1990-4Q", tMat.toFileString());
		assertEquals("Sun Sep 16 00:00:00 CEST 1990", tMat.getStartDate()
				.toString());

		tMat = tMat.getNextAsQuarter();
		assertEquals("1991-1Q", tMat.toFileString());

		tMat = mat; // return to start

		tMat = tMat.getPreviousAsQuarter();
		assertEquals("1989-4Q", tMat.toFileString());

		tMat = tMat.getPreviousAsQuarter();
		assertEquals("1989-3Q", tMat.toFileString());

		tMat = tMat.getPreviousAsQuarter();
		assertEquals("1989-2Q", tMat.toFileString());

		tMat = tMat.getPreviousAsQuarter();
		assertEquals("1989-1Q", tMat.toFileString());

		tMat = tMat.getPreviousAsQuarter();
		assertEquals("1988-4Q", tMat.toFileString());

	}

	@SuppressWarnings("static-method")
	@Test
	public void testTreeMap() {
		TreeMap<Maturity, String> tree = new TreeMap<>();
		Maturity mat = new Maturity(1000, (byte) 0);
		tree.put(mat, "first key");

		assertEquals(1, tree.size());

		Maturity other = new Maturity(1000, (byte) 0);

		String val = tree.get(other);

		assertEquals("first key", val);

	}

	@SuppressWarnings("static-method")
	@Test
	public void testMaturityString() {
		Maturity mat = new Maturity(2008, (byte) 8);
		assertEquals("U08", mat.toDataProviderMediumString());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMaturityLong() {
		Maturity mat = new Maturity(2000, (byte) 8);

		assertEquals("2000-3Q", mat.toFileString());

		GregorianCalendar gc = new GregorianCalendar();
		gc.clear();
		gc.set(1990, 0, 20); // January 20th 1990

		mat = new Maturity(gc);
		assertEquals("1990-1Q", mat.toFileString());
		assertEquals("H1990", mat.toDataProviderLongString());
		assertEquals("H0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // february
		mat = new Maturity(gc);
		assertEquals("1990-1Q", mat.toFileString());
		assertEquals("H1990", mat.toDataProviderLongString());
		assertEquals("H0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // march
		mat = new Maturity(gc);
		assertEquals("1990-2Q", mat.toFileString());
		assertEquals("M1990", mat.toDataProviderLongString());
		assertEquals("M0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // april
		mat = new Maturity(gc);
		assertEquals("1990-2Q", mat.toFileString());
		assertEquals("M1990", mat.toDataProviderLongString());
		assertEquals("M0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // may
		mat = new Maturity(gc);
		assertEquals("1990-2Q", mat.toFileString());
		assertEquals("M1990", mat.toDataProviderLongString());
		assertEquals("M0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // june
		mat = new Maturity(gc);
		assertEquals("1990-3Q", mat.toFileString());
		assertEquals("U1990", mat.toDataProviderLongString());
		assertEquals("U0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // july
		mat = new Maturity(gc);
		assertEquals("1990-3Q", mat.toFileString());
		assertEquals("U1990", mat.toDataProviderLongString());
		assertEquals("U0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // august
		mat = new Maturity(gc);
		assertEquals("1990-3Q", mat.toFileString());
		assertEquals("U1990", mat.toDataProviderLongString());
		assertEquals("U0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // september
		mat = new Maturity(gc);
		assertEquals("1990-4Q", mat.toFileString());
		assertEquals("Z1990", mat.toDataProviderLongString());
		assertEquals("Z0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // october
		mat = new Maturity(gc);
		assertEquals("1990-4Q", mat.toFileString());
		assertEquals("Z1990", mat.toDataProviderLongString());
		assertEquals("Z0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // november
		mat = new Maturity(gc);
		assertEquals("1990-4Q", mat.toFileString());
		assertEquals("Z1990", mat.toDataProviderLongString());
		assertEquals("Z0", mat.toDataProviderShortString());

		gc.add(Calendar.MONTH, 1); // december
		mat = new Maturity(gc);
		assertEquals("1991-1Q", mat.toFileString());
		assertEquals("H1991", mat.toDataProviderLongString());
		assertEquals("H1", mat.toDataProviderShortString());

		// but I am still in 1990!!!!
		assertEquals(1990, gc.get(Calendar.YEAR));
	}

}
