package com.mfg.tea.accounting;

import org.junit.Assert;
import org.junit.Test;

public class EquityTest {

	@SuppressWarnings("static-method")
	@Test
	public void test() {

		StockInfo sugarMaterial = new StockInfo("sugar", 3, 99);
		DuplexEquity de = new DuplexEquity(sugarMaterial);

		SingleInventory sugarInv = new SingleInventory(sugarMaterial, false);
		SimpleEquity longSugar = new SimpleEquity(sugarInv);

		SingleInventory sugarShortInv = new SingleInventory(sugarMaterial, true);
		SimpleEquity shortSugar = new SimpleEquity(sugarShortInv);

		de._addSimpleEquity(longSugar, true);
		de._addSimpleEquity(shortSugar, false);

		/*
		 * now let's try to make some trades, the register delta points method
		 * is already set up after a
		 */

		longSugar.registerDeltaPoints(100, 102);

		longSugar.registerDeltaPoints(200, 111);

		Assert.assertEquals(213, longSugar.getPoints());
		Assert.assertEquals(213, de.getPoints());

		Assert.assertEquals(213, de.getGainInPoints());

		/*
		 * Two trades on the short inventory.
		 */
		shortSugar.registerDeltaPoints(300, 12);
		shortSugar.registerDeltaPoints(400, -99);

		Assert.assertEquals(99,
				shortSugar.getCurrentDrawDownClosedEquityPoints());

		Assert.assertEquals(99, shortSugar.getMaxDrawDownClosedEquityPoints());

		Assert.assertEquals(213, longSugar.getPoints());
		Assert.assertEquals(12 - 99, shortSugar.getPoints());
		Assert.assertEquals(213 + 12 - 99, de.getPoints());

		/*
		 * Now I try to know the drawdown of the sum, I have done only one loss,
		 * so the maximum drawdown is equal to the current drawdown.
		 */
		Assert.assertEquals(99, de.getCurrentDrawDownClosedEquityPoints());
		Assert.assertEquals(99, de.getMaxDrawDownClosedEquityPoints());

		/*
		 * Ok, now the short sugar gains a little.
		 */
		shortSugar.registerDeltaPoints(500, 12);

		// the current drawdown is lower, also for the sum
		Assert.assertEquals(99 - 12,
				shortSugar.getCurrentDrawDownClosedEquityPoints());

		Assert.assertEquals(99 - 12, de.getCurrentDrawDownClosedEquityPoints());

		// the maximum drawdown is the same.
		Assert.assertEquals(99, shortSugar.getMaxDrawDownClosedEquityPoints());

		// also for the sum
		Assert.assertEquals(99, de.getMaxDrawDownClosedEquityPoints());

		/*
		 * The long sugar loses a little.
		 */
		longSugar.registerDeltaPoints(600, -21);

		// the short drawdown is the same
		Assert.assertEquals(99 - 12,
				shortSugar.getCurrentDrawDownClosedEquityPoints());

		// the long drawdown is raised.
		Assert.assertEquals(21,
				longSugar.getCurrentDrawDownClosedEquityPoints());

		Assert.assertEquals(99 - 12 + 21,
				de.getCurrentDrawDownClosedEquityPoints());

		Assert.assertEquals(99 - 12 + 21, de.getMaxDrawDownClosedEquityPoints());

		/*
		 * now the long sugar gains again
		 */
		longSugar.registerDeltaPoints(700, 9);

		// the short drawdown is the same
		Assert.assertEquals(99 - 12,
				shortSugar.getCurrentDrawDownClosedEquityPoints());

		// the long drawdown is lowered
		Assert.assertEquals(21 - 9,
				longSugar.getCurrentDrawDownClosedEquityPoints());

		Assert.assertEquals(99 - 12 + 21 - 9,
				de.getCurrentDrawDownClosedEquityPoints());

		// the maximum should be the same.
		Assert.assertEquals(99 - 12 + 21, de.getMaxDrawDownClosedEquityPoints());

		/*
		 * I now raise above water.
		 */
		longSugar.registerDeltaPoints(800, 3_000);

		/*
		 * the normal drawdown is zero, both for the long and the duplex.
		 */
		Assert.assertEquals(0, longSugar.getCurrentDrawDownClosedEquityPoints());

		Assert.assertEquals(0, de.getCurrentDrawDownClosedEquityPoints());

		// the maximum should be the same.
		Assert.assertEquals(99 - 12 + 21, de.getMaxDrawDownClosedEquityPoints());

		// and also the short drawdown should be the same.
		Assert.assertEquals(99 - 12,
				shortSugar.getCurrentDrawDownClosedEquityPoints());

	}

}
