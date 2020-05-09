package com.mfg.dm.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mfg.common.RealTick;
import com.mfg.dm.filters.FinalNotFinalClassificator.EAnswer;

public class FinalNotFinalClassificatorTest {

	@SuppressWarnings("static-method")
	@Test
	public void testVolume() {
		FinalNotFinalClassificator fnfc = new FinalNotFinalClassificator(10);

		RealTick rt = new RealTick(10, 200, true, 40);
		EAnswer res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// update the volume.
		int newVol = fnfc.onRealTimeQuoteVolumeUpdate(200, 70);
		assertEquals(110, newVol);

		rt.setPrice(190);
		rt.setPhysicalTime(11);
		rt.setVolume(5);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		newVol = fnfc.onRealTimeQuoteVolumeUpdate(190, 10);
		assertEquals(15, newVol);

		rt.setPrice(200);
		rt.setPhysicalTime(12);
		rt.setVolume(21);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		newVol = fnfc.onRealTimeQuoteVolumeUpdate(200, 10);
		assertEquals(31, newVol);

		// I have the v, so the next real price is not final,
		// this price is filtered and I simply have the
		rt.setPrice(190);
		rt.setPhysicalTime(13);
		rt.setVolume(10);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		/*
		 * I update the previous tick
		 */
		newVol = fnfc.onRealTimeQuoteVolumeUpdate(190, 10);
		assertEquals(35, newVol);

		// another ping pong
		rt.setPrice(200);
		rt.setPhysicalTime(14);
		rt.setVolume(15);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		newVol = fnfc.onRealTimeQuoteVolumeUpdate(200, 10);
		assertEquals(56, newVol);

		// I have the v, so the next real price is not final,
		// this price is filtered and I simply have the
		rt.setPrice(190);
		rt.setPhysicalTime(15);
		rt.setVolume(5);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		newVol = fnfc.getVolumeLastNotFinal(190);
		assertEquals(40, newVol);

		newVol = fnfc.onRealTimeQuoteVolumeUpdate(190, 10);
		assertEquals(50, newVol);

		/*
		 * Ok, now I exit from the leg.
		 */
		rt.setPrice(180);
		rt.setPhysicalTime(16);
		rt.setReal(false);
		rt.setVolume(1000);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.PREVIOUS_AND_THIS_ARE_FINAL, res);

		RealTick rtCheck = fnfc.getPreviousFinalTick();
		assertEquals(rtCheck, new RealTick(15, 190, true, 50));
	}

	@SuppressWarnings("static-method")
	@Test
	/**
	 * This bug is present when I exit the V state with a fake price after that 
	 * I have had a not final price: I need that the filters gives me the not final price as final 
	 */
	public void testFakeBug() {
		FinalNotFinalClassificator fnfc = new FinalNotFinalClassificator(10);

		RealTick rt = new RealTick(10, 200, true);
		EAnswer res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		rt.setPrice(190);
		rt.setPhysicalTime(11);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		rt.setPrice(200);
		rt.setPhysicalTime(12);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// I have the v, so the next real price is not final
		rt.setPrice(190);
		rt.setPhysicalTime(13);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		// I exit the range from below with a fake price, I want the previous
		// tick as final
		rt.setPrice(180);
		rt.setPhysicalTime(14);
		rt.setReal(false);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.PREVIOUS_AND_THIS_ARE_FINAL, res);

		RealTick rtCheck = fnfc.getPreviousFinalTick();
		assertEquals(rtCheck, new RealTick(13, 190, true));

	}

	@SuppressWarnings("static-method")
	@Test
	public void test() {
		FinalNotFinalClassificator fnfc = new FinalNotFinalClassificator(10);

		RealTick rt = new RealTick(10, 200, false);
		EAnswer res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// I now put another tick, with a back time
		rt.setPhysicalTime(9);
		boolean ok = false;
		try {
			fnfc.acceptTick(rt);
		} catch (IllegalArgumentException e) {
			ok = true;
		}
		assertTrue(ok);

		// the tick is wrong
		rt.setPrice(188);
		rt.setPhysicalTime(11);
		ok = false;
		try {
			fnfc.acceptTick(rt);
		} catch (IllegalArgumentException e) {
			ok = true;
		}

		assertTrue(ok);

		// Ok, now let's go on

		// RealTick rt = new RealTick(10, 200, false);
		rt.setPrice(210);
		rt.setPhysicalTime(11);
		rt.setReal(true);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// another real tick
		rt.setPrice(220);
		rt.setPhysicalTime(12);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// Now the filter should be in the 2 real prices state
		// I return to 210
		rt.setPrice(210);
		rt.setPhysicalTime(13);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// Ok I am in the V, actually a ^, so let's try, 210 and 220 should not
		// be final
		rt.setPrice(220);
		rt.setPhysicalTime(14);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		rt.setPrice(210);
		rt.setPhysicalTime(15);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		// then I exit the range, I cannot exit the range with a gap...
		rt.setPrice(230);
		rt.setPhysicalTime(16);
		try {
			ok = false;
			res = fnfc.acceptTick(rt);
		} catch (IllegalArgumentException e) {
			ok = true;
		}
		assertTrue(ok);

		// so I go first to 220
		rt.setPrice(220);
		rt.setPhysicalTime(16);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		// and then to 230
		rt.setPrice(230);
		rt.setPhysicalTime(17);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.PREVIOUS_AND_THIS_ARE_FINAL, res);

		// Ok, let's get the previous final
		RealTick prevFinal = fnfc.getPreviousFinalTick();
		assertEquals(220, prevFinal.getPrice());
		assertEquals(16, prevFinal.getPhysicalTime());

		// Ok, now I should have the 220-230 route, let's give a price real
		// without forming a route
		rt.setPrice(240);
		rt.setPhysicalTime(18);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// Now I am on the 230-240 route, I form a ^ returning to 230
		rt.setPrice(230);
		rt.setPhysicalTime(19);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// I return to 220, and I exit the ^ from below, but without two final
		// ticks
		rt.setPrice(220);
		rt.setPhysicalTime(20);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// now the route is 230->220, return to 230 forming a V this time
		rt.setPrice(230);
		rt.setPhysicalTime(21);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_FINAL, res);

		// Now the 220 and 230 marks are not final
		rt.setPrice(220);
		rt.setPhysicalTime(22);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

		rt.setPrice(230);
		rt.setPhysicalTime(23);
		res = fnfc.acceptTick(rt);
		assertEquals(EAnswer.THIS_TICK_IS_NOT_FINAL, res);

	}

}
