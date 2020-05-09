package com.mfg.dm.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mfg.common.Tick;
import com.mfg.dm.filters.LonelyTicksFilter.EAnswer;

public class LonelyTicksFilterTest {

	@Test
	@SuppressWarnings("static-method")
	public void testAcceptTick_afterSpike() {
		LonelyTicksFilter ltf = new LonelyTicksFilter(2, 5);

		Tick aTick = new Tick(0, 50);
		EAnswer res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_OK);

		aTick = new Tick(1, 10);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_IN_JAIL);

		// a spike and I return to the same level as before
		aTick = new Tick(2, 50);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_IN_JAIL);

		/*
		 * now this tick is OK, and also the previous one (2) is OK,
		 * unfortunately 2 is equal in price to the last OK tick (0) so it must
		 * be discarded by the filter.
		 */
		aTick = new Tick(3, 52);
		res = ltf.acceptTick(aTick);
		assertEquals(EAnswer.THIS_TICK_IS_OK, res);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testAcceptTick() {
		LonelyTicksFilter ltf = new LonelyTicksFilter(2, 5);

		Tick aTick = new Tick(0, 50);
		EAnswer res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_OK);

		// no gap
		aTick = new Tick(1, 52);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_OK);

		// 2 tick gap
		aTick = new Tick(2, 48);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_OK);

		// a sudden spike?
		aTick = new Tick(3, 60);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_IN_JAIL);

		// no, return to normal
		aTick = new Tick(4, 64);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_AND_PREVIOUS_TICKS_ARE_OK);

		aTick = ltf.getPreviousTick();
		assertEquals(aTick, new Tick(3, 60));

		// a spike below
		aTick = new Tick(5, 44);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_IN_JAIL);

		// yes this is a real spike
		aTick = new Tick(6, 66);
		res = ltf.acceptTick(aTick);
		assertTrue(res == EAnswer.THIS_TICK_IS_IN_JAIL);

		aTick = new Tick(7, 68);
		res = ltf.acceptTick(aTick);
		assertEquals(EAnswer.THIS_AND_PREVIOUS_TICKS_ARE_OK, res);

		// two spikes one after another
		aTick = new Tick(8, 88);
		res = ltf.acceptTick(aTick);
		assertEquals(EAnswer.THIS_TICK_IS_IN_JAIL, res);

		aTick = new Tick(9, 108);
		res = ltf.acceptTick(aTick);
		assertEquals(EAnswer.THIS_TICK_IS_IN_JAIL, res);

		aTick = new Tick(10, 110);
		res = ltf.acceptTick(aTick);
		assertEquals(EAnswer.THIS_AND_PREVIOUS_TICKS_ARE_OK, res);

		aTick = ltf.getPreviousTick();
		assertEquals(aTick, new Tick(9, 108));
	}

}
