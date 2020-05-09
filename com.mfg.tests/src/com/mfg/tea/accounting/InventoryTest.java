package com.mfg.tea.accounting;

import org.junit.Assert;
import org.junit.Test;

import com.mfg.common.TEAException;

public class InventoryTest {

	@SuppressWarnings({ "static-method", "unused" })
	@Test
	public void testHierarchy() throws TEAException {
		//

		/*
		 * I create the root for all.
		 */
		LogicalInventoriesHolder root = new LogicalInventoriesHolder("root",
				null);

		/*
		 * Then I create a mixed folder on that logical one
		 */
		MixedInventoriesFolder rootMix = new MixedInventoriesFolder("MixRoot",
				null);
		root.addChild("MixRoot", rootMix);

		StockInfo juice = new StockInfo("juice", 5, 12);

		/*
		 * Another material.
		 */
		StockInfo milk = new StockInfo("Milk", 25, 66);

		/*
		 * Ok, now I have a folder for these materials, this folder will contain
		 * a global equity about the trading of juice and milk together.
		 * 
		 * This is the root for all my test.
		 */
		MixedInventoriesFolder mif = new MixedInventoriesFolder("test", rootMix);

		/**
		 * I have the inventory that holds all the juice "stocks", it is
		 * homogeneous.
		 */
		HomogeneusInventoriesFolder homogeneousJuice = new HomogeneusInventoriesFolder(
				juice, mif);

		/*
		 * I create two duplex inventories which I will add to the folder and
		 * then I will check that the statistics are correctly added.
		 */
		DuplexInventory juiceInventory = new DuplexInventory(mif, juice);
		/*
		 * I have a second juice inventory used to test the adding of the
		 * quantities. This will be another child of the homogeneous folder.
		 * Note that the constructor is the same, because this means that the
		 * second juice inventory is a child... at the same level
		 * 
		 * but this is an error, because I cannot create two children at the
		 * same level...
		 */
		DuplexInventory secondJuiceInv = new DuplexInventory(mif, juice);

		DuplexInventory milkInventory = new DuplexInventory(mif, milk);

		/*
		 * Then I have some transactions and I test that the equity changes.
		 */

		/*
		 * buy some juice
		 */
		Transaction aTransaction = new Transaction(10, 95_05);
		juiceInventory.newTransaction(true, aTransaction);

		/*
		 * Ok, I have done the transaction on a leaf inventory. The homogeneous
		 * folder should have it registered.
		 */
		Assert.assertEquals(10, homogeneousJuice.getQuantity());

		/*
		 * this is a homogeneous folder, so it has a quantity.
		 */
		Assert.assertEquals(10, homogeneousJuice.getStats().getQuantity());

		/*
		 * sell some juice
		 */
		aTransaction = new Transaction(-3, 97_05);
		juiceInventory.newTransaction(true, aTransaction);

		Assert.assertEquals(7, juiceInventory.getQuantity());

		Assert.assertEquals(7, homogeneousJuice.getQuantity());
		Assert.assertEquals(7, homogeneousJuice.getStats().getQuantity());
		/*
		 * Now the equity in points...
		 */
		long pl = juiceInventory.getStats().getPoints();
		Assert.assertEquals(3 * (9705 - 9505), pl);

		long plMoney = juiceInventory.getStats().getEquity();

		/*
		 * Ok, now the long and short equity
		 */
		long longPlMoney = juiceInventory.getStats().getLongStatistics()
				.getEquity();

		Assert.assertEquals(pl / 5 * 12, plMoney);
		Assert.assertEquals(pl / 5 * 12, longPlMoney);

		long homgMoney = homogeneousJuice.getStats().getEquity();

		Assert.assertEquals(plMoney, homgMoney);

		/*
		 * this is the equity in money.
		 */
		long realizedPL = mif.getStats().getEquity();

		Assert.assertEquals(plMoney, realizedPL);

		realizedPL = rootMix.getStats().getEquity();
		Assert.assertEquals(plMoney, realizedPL);

		/*
		 * Ok, now some milk transaction
		 */
		aTransaction = new Transaction(-3, 80_25);
		milkInventory.newTransaction(false, aTransaction);

		Assert.assertEquals(-3, milkInventory.getQuantity());
		Assert.assertEquals(0, milkInventory.getStats().getEquity());

		/*
		 * I was short of milk, now I open a long position
		 */
		aTransaction = new Transaction(15, 80_75);
		milkInventory.newTransaction(true, aTransaction);

		Assert.assertEquals(12, milkInventory.getQuantity());
		Assert.assertEquals(0, milkInventory.getStats().getEquity());

		/*
		 * I buy some milk to close a bit the position.
		 */
		aTransaction = new Transaction(1, 81_00);
		milkInventory.newTransaction(false, aTransaction);

		Assert.assertEquals(13, milkInventory.getQuantity());

		/*
		 * I have made some loss
		 */
		plMoney -= 66 * 3; // 66 is the value in ticks, I have lost 3 ticks.

		realizedPL = mif.getStats().getEquity();
		Assert.assertEquals(plMoney, realizedPL);

		realizedPL = rootMix.getStats().getEquity();
		Assert.assertEquals(plMoney, realizedPL);

		// I have lost three ticks in the milk trading.
		Assert.assertEquals(-66 * 3, milkInventory.getStats().getEquity());

		/*
		 * test of the mixed long and short equities
		 */
		long longMixedEquity = mif.getStats().getLongStatistics().getEquity();
		long shortMixedEquity = mif.getStats().getShortStatistics().getEquity();

		Assert.assertEquals(plMoney, longMixedEquity + shortMixedEquity);
		Assert.assertEquals(1440, longMixedEquity);
		Assert.assertEquals(-198, shortMixedEquity);

	}

	/*
	 * This is a test for the short type
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testShort() {
		/*
		 * the tick size is 5 and the each tick values 5, so the point value is
		 * 1.
		 */
		StockInfo si = new StockInfo("bund", 5, 5);
		DuplexInventory di = new DuplexInventory(si);

		Transaction aTransaction = new Transaction(-10, 95_05);
		di.newTransaction(false, aTransaction);

		Assert.assertEquals(-10, di.getQuantity());

		// / shorter.

		aTransaction = new Transaction(-7, 94_15);
		di.newTransaction(false, aTransaction);

		Assert.assertEquals(-17, di.getQuantity());

		/*
		 * The total cost should be on the other side
		 */

		// long totalCost = di.getTotalCost();

		Assert.assertEquals((94_15 * -7 + -10 * 95_05), di.getTotalCost());

		// System.out.println("tot cost " + totalCost);

		/*
		 * Ok, now I try a contrary movement which makes the first full and the
		 * second not, for one it is a gain for the other it is a lose.
		 */
		aTransaction = new Transaction(12, 94_40);
		di.newTransaction(false, aTransaction);

		Assert.assertEquals(-5, di.getQuantity());

		long pl = di.getStats().getEquity();

		/*
		 * The profit should be from the first and the second stock.
		 */
		long expPL = (95_05 - 94_40) * 10 + (94_15 - 94_40) * 2;

		// System.out.println("pl " + pl + " exp " + expPL);

		Assert.assertEquals((94_15 * -5), di.getTotalCost());
		Assert.assertEquals(expPL, pl);

		int tickPl = di.getStats().getEquityTicks();

		Assert.assertEquals(expPL / 5, tickPl);
	}

	@Test
	@SuppressWarnings("static-method")
	public void test1() {

		/*
		 * This means that a tick is 10 units (in this case cents) and each tick
		 * values 10,000 units of money, that is 100$.
		 */
		StockInfo si = new StockInfo("oil", 10, 10_000);

		DuplexInventory di = new DuplexInventory(si);
		/*
		 * I buy one batch of 75 barrels of oil whose single price is 100.10$
		 */
		Transaction aTransaction = new Transaction(75, 100_10);
		di.newTransaction(true, aTransaction);

		Assert.assertEquals(75, di.getQuantity());

		Assert.assertEquals(75 * 100_10 * 10_00, di.getTotalCost());

		/*
		 * Ok, now the oil changes price. So the market value of my position
		 * changes accordingly. This is also known as the open trade equity.
		 */
		long pl = di.getUnrealizedPL(100_20);

		/*
		 * The profit and loss should be 10$ * quantity held... 10*75 = 750$,
		 * this is correct but I would like to have back 750_00, that is 75,000,
		 * because I have cents, not dollars.
		 * 
		 * 750000
		 */

		Assert.assertEquals(750_000, pl);

		/*
		 * Ok, now I buy another stock at another price.
		 */
		aTransaction = new Transaction(30, 100_30);
		di.newTransaction(true, aTransaction);

		/*
		 * Ok, now the quantity is the sum
		 */
		Assert.assertEquals(105, di.getQuantity());

		/*
		 * the total cost is the sum.
		 */
		Assert.assertEquals((100_30 * 30 + 75 * 100_10) * 10_00,
				di.getTotalCost());

		// double totalCostOld = di.getTotalCost();

		/*
		 * Ok, now we start making a complex thing, I sell 40 units of oil at a
		 * certain price, which is higher than the older but lower than the
		 * higher.
		 */
		aTransaction = new Transaction(-40, 100_20);
		di.newTransaction(true, aTransaction);

		pl = di.getStats().getEquity();

		/*
		 * First of all the quantity!
		 */
		Assert.assertEquals(65, di.getQuantity());

		/*
		 * The total cost is based on the remaining 35 items in the first item
		 * and all the 30 items in the other.
		 */
		Assert.assertEquals((100_10 * 35 + 30 * 100_30) * 10_00,
				di.getTotalCost());

		// double totalCostNew = di.getTotalCost();

		// System.out.println("realized pl " + pl + " difference in cost "
		// + (totalCostNew - totalCostOld));
	}
}
