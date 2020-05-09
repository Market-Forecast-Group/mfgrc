package com.mfg.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.mfg.common.Tick;
import com.mfg.utils.IndexedRandomTickSource;

/**
 * Simple test for the indexed random source.
 * 
 * <p>
 * This test will try to reproduce the normal use case of this class, which is a
 * (seemingly) infinitely stream of ticks which can be replayed perfectly, as if
 * it were stored in its entirety.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class IndexedRandomTickSourceTest {

	@SuppressWarnings("static-method")
	@Test
	public void testRandom() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		java.lang.reflect.Field seedField = Random.class
				.getDeclaredField("seed");

		seedField.setAccessible(true);

		Random testR = new Random(33);
		AtomicLong before = (AtomicLong) seedField.get(testR);
		long val = before.get();

		double random = testR.nextDouble();

		// seedField.set(testR, before);
		// You have the reference, so you can alter the Random as you wish :)
		before.set(val);

		double randomAfter = testR.nextDouble();

		assertEquals(random, randomAfter, 0);

	}

	@SuppressWarnings("static-method")
	@Test
	public void testWithIndex() {
		// I start in a definite time, for example January 1st, 2013
		GregorianCalendar gc = new GregorianCalendar(2013, 0, 1);

		IndexedRandomTickSource irts = new IndexedRandomTickSource(88, 10,
				gc.getTimeInMillis());

		GregorianCalendar end = new GregorianCalendar(2013, 5, 22);
		long endMillis = end.getTimeInMillis();

		irts.buildIndexUpTo(endMillis, 100_000);
		irts.reset();

		Tick tk = new Tick();
		do {
			irts.putNextTick(tk);
		} while (tk.getPhysicalTime() < endMillis);

		assertTrue(tk.getPhysicalTime() >= endMillis);
		System.out.println("The last tick sent is " + tk);

		Tick compTick = new Tick();
		GregorianCalendar midCal = new GregorianCalendar(2013, 3, 11);
		irts.goTo(midCal.getTimeInMillis());

		irts.putNextTick(compTick);
		System.out.println("First tick after mid is " + compTick
				+ " you asked " + midCal.getTime());
		long delta = compTick.getPhysicalTime() - midCal.getTimeInMillis();
		System.out.println("Raw " + compTick.getPhysicalTime() + " mid "
				+ midCal.getTimeInMillis() + " delta " + delta);
		assertTrue(compTick.getPhysicalTime() >= midCal.getTimeInMillis());

		do {
			irts.putNextTick(compTick);
		} while (compTick.getPhysicalTime() < endMillis);

		System.out.println("Last tick compared is " + compTick);

		assertEquals(compTick, tk);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testWithoutIndex() {
		// I start in a definite time, for example January 1st, 2013
		GregorianCalendar gc = new GregorianCalendar(2013, 0, 1);

		IndexedRandomTickSource irts = new IndexedRandomTickSource(88, 10,
				gc.getTimeInMillis());

		GregorianCalendar end = new GregorianCalendar(2013, 5, 22);
		long endMillis = end.getTimeInMillis();
		Tick tk = new Tick();
		do {
			irts.putNextTick(tk);
		} while (tk.getPhysicalTime() < endMillis);

		assertTrue(tk.getPhysicalTime() >= endMillis);
		System.out.println("The last tick sent is " + tk);

		Tick compTick = new Tick();
		GregorianCalendar midCal = new GregorianCalendar(2013, 3, 11);
		irts.goTo(midCal.getTimeInMillis());

		irts.putNextTick(compTick);
		System.out.println("First tick after mid is " + compTick
				+ " you asked " + midCal.getTime());
		long delta = compTick.getPhysicalTime() - midCal.getTimeInMillis();
		System.out.println("Raw " + compTick.getPhysicalTime() + " mid "
				+ midCal.getTimeInMillis() + " delta " + delta);
		assertTrue(compTick.getPhysicalTime() >= midCal.getTimeInMillis());

		do {
			irts.putNextTick(compTick);
		} while (compTick.getPhysicalTime() < endMillis);

		System.out.println("Last tick compared is " + compTick);

		assertEquals(compTick, tk);

	}

}
