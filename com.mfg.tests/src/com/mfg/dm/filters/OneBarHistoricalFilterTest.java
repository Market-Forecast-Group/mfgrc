package com.mfg.dm.filters;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.mfg.common.RealTick;

//import com.mfg.dm.filters.OneBarHistoricalFilter.EAnswer;

public class OneBarHistoricalFilterTest {

	@SuppressWarnings("static-method")
	@Test
	public void testBugMfg3() {
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(25);
		RealTick tk = new RealTick(1408025235000L, 194475, true, 10);
		obhf.acceptTick(tk);
		tk = new RealTick(1408025250000L, 194500, true, 13); // 2lowbar 10 13
		obhf.acceptTick(tk);
		tk = new RealTick(1408025251500L, 194475, false, 11); // Tbar 10 13 11
		obhf.acceptTick(tk);
		tk = new RealTick(1408025253000L, 194500, false, 17); // 2lowbar 21
		obhf.acceptTick(tk);
		tk = new RealTick(1408025254500L, 194475, false, 12); // tbar
		obhf.acceptTick(tk);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testBugMfg1() {

		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(5);

		RealTick tk = new RealTick(1387643415000L, 4395, true, 10);
		obhf.acceptTick(tk);
		tk = new RealTick(1387643421000L, 4400, true, 29);
		obhf.acceptTick(tk);
		tk = new RealTick(1387643427000L, 4395, true, 31);
		obhf.acceptTick(tk);
		tk = new RealTick(1387643433000L, 4400, true, 13);
		obhf.acceptTick(tk);
		tk = new RealTick(1387643439000L, 4405, false, 19);
		obhf.acceptTick(tk);
		tk = new RealTick(1387643445000L, 4410, true, 5);
		obhf.acceptTick(tk);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testBugMfg2() {

		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(5);

		RealTick tk = new RealTick(1387656742500L, 4590, false, 11);
		obhf.acceptTick(tk);

		tk = new RealTick(1387656750000L, 4595, false, 13); // 24
		obhf.acceptTick(tk);

		tk = new RealTick(1387656757500L, 4590, false, 7); // 31
		obhf.acceptTick(tk);

		tk = new RealTick(1387656765000L, 4595, true, 15); // 46
		obhf.acceptTick(tk);

		tk = new RealTick(1387656780000L, 4590, true, 4); // 50
		obhf.acceptTick(tk);

		tk = new RealTick(1387656795000L, 4595, true, 10); // 60
		obhf.acceptTick(tk);

		tk = new RealTick(1387656810000L, 4590, true, 12); // 72
		obhf.acceptTick(tk);

	}

	@SuppressWarnings("static-method")
	@Test
	public void testDownBar() {

		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, false, 10);
		List<RealTick> list;
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(15);
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// down tick I make the first bar
		rt.setPhysicalTime(12);
		rt.setPrice(180);
		rt.setVolume(67);
		list = obhf.acceptTick(rt);
		assertEquals(2, list.size());

		RealTick check;
		check = list.get(0);
		assertEquals(check, new RealTick(10, 200, false, 10));
		check = list.get(1);
		assertEquals(check, new RealTick(11, 190, false, 15));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTDownBarOldOpen() {
		// no open overwrite
		// I test the down bar with open substituted.
		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, false, 10);
		List<RealTick> list;
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(20);
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// Up tick I make a t
		rt.setPhysicalTime(12);
		rt.setPrice(200);
		rt.setVolume(16);
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// I go down with a real
		rt.setPhysicalTime(13);
		rt.setPrice(190);
		rt.setReal(true);
		rt.setVolume(5);
		list = obhf.acceptTick(rt);
		assertEquals(list, null);
		// assertEquals(51, obhf.getAccumulatedVolume());

		rt.setReal(false);

		// then up again
		rt.setPhysicalTime(14);
		rt.setPrice(200);
		rt.setVolume(10);
		list = obhf.acceptTick(rt);
		assertEquals(list, null);
		// assertEquals(61, obhf.getAccumulatedVolume());

		// then I exit the range.
		rt.setPhysicalTime(15);
		rt.setPrice(210);
		rt.setVolume(19);
		list = obhf.acceptTick(rt);
		assertEquals(3, list.size());
		// only the open!
		// assertEquals(10, obhf.getAccumulatedVolume());

		RealTick check;
		check = list.get(0);
		// the open is not overwritten
		assertEquals(check, new RealTick(10, 200, false, 26));
		check = list.get(1);
		assertEquals(check, new RealTick(13, 190, true, 25));
		check = list.get(2);
		assertEquals(check, new RealTick(14, 200, false, 10));

	}

	@SuppressWarnings("static-method")
	@Test
	public void testRealWithOverwrite() {
		// I test the down bar with open substituted.
		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, true, 13);
		List<RealTick> list;
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(18);
		list = obhf.acceptTick(rt); // 200 13 190 18 tot 31
		assertEquals(list, null);

		// Up tick I make a t, and the new tick is real
		rt.setPhysicalTime(12);
		rt.setPrice(200);
		rt.setVolume(12);
		list = obhf.acceptTick(rt); // 200 25 190 18 tot 43
		assertEquals(list, null);

		// then a Ping pong with two fake prices.
		// rt.setReal(false);

		rt.setPhysicalTime(13);
		rt.setPrice(190);
		rt.setVolume(11);
		list = obhf.acceptTick(rt); // 200 25 190 29 tot 54
		assertEquals(list, null);

		rt.setPhysicalTime(14);
		rt.setPrice(200);
		rt.setVolume(71);
		list = obhf.acceptTick(rt); // 200 96 190 29 tot 125
		assertEquals(list, null);

		// I exit forming a three bar
		rt.setPhysicalTime(15);
		rt.setPrice(210);
		rt.setVolume(16);
		list = obhf.acceptTick(rt);
		assertEquals(3, list.size());

		RealTick check;
		check = list.get(0);
		assertEquals(check, new RealTick(10, 200, true, 25));
		check = list.get(1);
		assertEquals(check, new RealTick(11, 190, true, 29));
		check = list.get(2);
		assertEquals(check, new RealTick(14, 200, true, 71));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testRealNotOverwrite() {
		// I test the down bar with open substituted.
		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, true, 19);
		List<RealTick> list;
		list = obhf.acceptTick(rt); // 200 19 190 0 tot 19
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(15);
		list = obhf.acceptTick(rt); // 200 19 190 15 tot 34
		assertEquals(list, null);

		// Up tick I make a t, and the new tick is real
		rt.setPhysicalTime(12);
		rt.setPrice(200);
		rt.setVolume(14);
		list = obhf.acceptTick(rt); // 200 33 190 15 tot 48
		assertEquals(list, null);

		// then a Ping pong with two fake prices.
		rt.setReal(false);

		rt.setPhysicalTime(13);
		rt.setPrice(190);
		rt.setVolume(18);
		list = obhf.acceptTick(rt); // 200 33 190 33 tot 66
		assertEquals(list, null);

		rt.setPhysicalTime(14);
		rt.setPrice(200);
		rt.setVolume(21);
		list = obhf.acceptTick(rt); // 200 54 190 33 tot 87
		assertEquals(list, null);

		// I exit forming a three bar
		rt.setPhysicalTime(15);
		rt.setPrice(210);
		list = obhf.acceptTick(rt);
		assertEquals(3, list.size());

		RealTick check;
		check = list.get(0);
		assertEquals(check, new RealTick(10, 200, true, 33));
		check = list.get(1);
		assertEquals(check, new RealTick(11, 190, true, 33));
		check = list.get(2);
		assertEquals(check, new RealTick(12, 200, true, 21));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTDownBarNewOpenNewT() {
		// I test the down bar with open substituted.
		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, false, 11);
		List<RealTick> list;
		list = obhf.acceptTick(rt); // 200 11 190 0 tot 11
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(17);
		list = obhf.acceptTick(rt); // 200 11 190 17 tot 28
		assertEquals(list, null);

		// Up tick I make a t, and the new tick is real
		rt.setPhysicalTime(12);
		rt.setPrice(200);
		rt.setReal(true);
		rt.setVolume(13);
		list = obhf.acceptTick(rt); // 200 24 190 17 tot 41
		assertEquals(list, null);

		// at this point I have rewritten the open

		// I go down, with a new low
		rt.setPhysicalTime(13);
		rt.setPrice(190);
		rt.setReal(false);
		rt.setVolume(12);
		list = obhf.acceptTick(rt); // 200 24 190 29 tot 53
		assertEquals(list, null);

		// up again with a new up real
		rt.setPhysicalTime(14);
		rt.setPrice(200);
		rt.setReal(true);
		rt.setVolume(16);
		list = obhf.acceptTick(rt); // 200 40 190 29 tot 69
		assertEquals(list, null);

		// exit the range
		rt.setPhysicalTime(15);
		rt.setPrice(210);
		rt.setReal(true);
		rt.setVolume(14);
		list = obhf.acceptTick(rt); // 210 14 ... tot 14
		assertEquals(3, list.size());

		RealTick check;
		check = list.get(0);
		assertEquals(check, new RealTick(12, 200, true, 24));
		check = list.get(1);
		assertEquals(check, new RealTick(13, 190, false, 29));
		check = list.get(2);
		assertEquals(check, new RealTick(14, 200, true, 16));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTDownBarNewOpen() {
		// I test the down bar with open substituted.
		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, false, 11);
		List<RealTick> list;
		list = obhf.acceptTick(rt); // 200 11 190 0 tot 11
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(17);
		list = obhf.acceptTick(rt); // 200 11 190 17 tot 28
		assertEquals(list, null);

		// Up tick I make a t, and the new tick is real
		rt.setPhysicalTime(12);
		rt.setPrice(200);
		rt.setReal(true);
		rt.setVolume(13); // 200 24 190 17 tot 41
		list = obhf.acceptTick(rt);
		assertEquals(list, null);

		// at this point I have rewritten the open

		// I go down, with a new low
		rt.setPhysicalTime(13);
		rt.setPrice(190);
		rt.setReal(false);
		rt.setVolume(12);
		list = obhf.acceptTick(rt); // 200 24 190 29 tot 53
		assertEquals(list, null);

		// new bar
		rt.setPhysicalTime(14);
		rt.setPrice(180);
		rt.setReal(false);
		rt.setVolume(191);
		list = obhf.acceptTick(rt); // 180 191 ..... tot 191
		assertEquals(2, list.size());

		RealTick check;
		check = list.get(0);
		assertEquals(check, new RealTick(12, 200, true, 24));
		check = list.get(1);
		assertEquals(check, new RealTick(13, 190, false, 29));

	}

	@SuppressWarnings("static-method")
	@Test
	public void testTDownBar() {
		// the simplest case one down bar
		OneBarHistoricalFilter obhf = new OneBarHistoricalFilter(10);

		RealTick rt = new RealTick(10, 200, false, 13);
		List<RealTick> list;
		list = obhf.acceptTick(rt); // 200 13 190 0 tot 13
		assertEquals(list, null);

		// down tick, I make a range
		rt.setPhysicalTime(11);
		rt.setPrice(190);
		rt.setVolume(17);
		list = obhf.acceptTick(rt); // 200 13 190 17 tot 30
		assertEquals(list, null);

		// Up tick I make a t
		rt.setPhysicalTime(12);
		rt.setPrice(200);
		rt.setVolume(15);
		list = obhf.acceptTick(rt); // 200 28 190 17 tot 45
		assertEquals(list, null);

		// Up tick I exit the range, completing the bar
		rt.setPhysicalTime(13);
		rt.setPrice(210);
		rt.setVolume(12);
		list = obhf.acceptTick(rt); // 210 13 ... tot 213
		assertEquals(3, list.size());

		// Ok, now I check the ticks
		RealTick check;
		check = list.get(0);
		assertEquals(check, new RealTick(10, 200, false, 13));
		check = list.get(1);
		assertEquals(check, new RealTick(11, 190, false, 17));
		check = list.get(2);
		assertEquals(check, new RealTick(12, 200, false, 15));

	}

}
