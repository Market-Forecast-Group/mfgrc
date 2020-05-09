package com.mfg.tea.accounting;

//import com.mfg.tea.conn.DuplexInventory;
import com.mfg.common.TEAException;
import com.mfg.utils.U;

/**
 * A class that holds two <i>single</i> inventories, one that is referred as the
 * <i>long</i> and another which is the <i>short</i> inventory.
 * 
 * <p>
 * An inventory has not a child. It has no sense, physical, to have an oil stock
 * which has a child gold stock. But can have homogeneous children and parents,
 * made of the same "stuff" (the same symbol); But this is modeled using the
 * {@link HomogeneusInventoriesFolder}
 * 
 * <p>
 * So an inventory may have children and parents of the same kind which may or
 * may <b>not</b> be related to children in the TEA sense... this means for
 * example that we have a portfolio in a MFG which is trading gold and oil. This
 * portfolio has a total equity based on two different stock types.
 * 
 * <p>
 * Then we may be interested in the overall gain or loss based on all the gold
 * and oil accounts in the system, from all the TEAs around, also remote.
 * 
 * <p>
 * This means really that we have two different trees, the equity tree and the
 * stock tree.
 * 
 * <p>
 * This class is the only class that receives a new quote and it will pass it to
 * all its children. The closed trade equity is updated upward, the open trade
 * equity is updated downward instead.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public final class DuplexInventory extends HomogeneousInventoryBase {

	@Override
	public String toString() {
		return "[ ql: " + _longInventory.getQuantity() + " qs: "
				+ _shortInventory.getQuantity() + "]";
	}

	/*
	 * 
	 * 
	 * The only physical meaning is to combine the numerical quantities, that is
	 * the financial aspect of the inventory: cost, gain, etc... or, better, the
	 * movement of the implicit equity account associated with this inventory.
	 * 
	 * I have to define how this may be implemented.
	 * 
	 * There are certainly some accounts which are only financial, in the sense
	 * that their movement is based only on transactions which happens in stock
	 * children accounts.
	 * 
	 * Maybe I have to enforce that.
	 */

	/**
	 * Builds an inventory object able to track transactions.
	 * 
	 * @param aParent
	 *            the parent inventory. If it is not null it <b>must</b> have
	 *            the same stock information.
	 * 
	 * @param aStockInfo
	 *            the information used to track the stock movements inside this.
	 */
	public DuplexInventory(MixedInventoriesFolder parent, StockInfo aStockInfo) {
		super(new DuplexEquity(aStockInfo), parent, aStockInfo);
		_longInventory = new SingleInventory(aStockInfo, false);
		_shortInventory = new SingleInventory(aStockInfo, true);

		/*
		 * The duplex inventory has a double equity responsible for the long and
		 * the short accounts. This cast is OK because we have created the
		 * equity on the constructor (see above line).
		 */
		((DuplexEquity) _equity)._addSimpleEquity(_longInventory._equity, true);
		((DuplexEquity) _equity)._addSimpleEquity(_shortInventory._equity,
				false);
	}

	/**
	 * Builds an inventory with no parent.
	 * 
	 * @param si
	 *            the stock info which is responsible to know the "matter"
	 *            traded in this inventory.
	 */
	public DuplexInventory(StockInfo si) {
		this(null, si);
	}

	/*
	 * The duplex inventory has a "cost" associated to it, which is the cost
	 * sustained to make this inventory. This cost may be "negative" because I
	 * may be "short" of a quantity and this means that I have already sold
	 * something which I did not have.
	 * 
	 * Apart from the cost I have also a "gain" associated to it which is the
	 * probable gain which I would have if I wanted to liquidate the inventory
	 * now. Of course the gain may also be negative (that is a cost) if I am
	 * short of this commodity.
	 * 
	 * The cost is dependent on the history of the inventory, because it depends
	 * on the individual cost of the items that I have bought (or sold), it does
	 * not depend on the prices of the contrary transactions (they influence
	 * only the equity).
	 * 
	 * The probable gain (or loss) depends only on the current state of the
	 * inventory and the current market price. It does not depend on history.
	 * 
	 * The cumulative gain (or loss) instead depends ONLY on the history of the
	 * transactions.
	 * 
	 * 
	 * The confusion and complexity is because there are some things that needs
	 * the history and something which do not.
	 */

	/**
	 * This stores all the "longs" transactions.
	 */
	private final SingleInventory _longInventory;

	/**
	 * This handles all the shorts transactions.
	 */
	private final SingleInventory _shortInventory;

	/**
	 * records a simple transaction which is on this account.
	 * 
	 * <p>
	 * The transaction can be long or short, that is it can be relative to the
	 * short inventory or the long inventory.
	 * 
	 * <p>
	 * The transaction is a double entry transaction. The second account which
	 * is debited/credited is always the local equity of this inventory.
	 * 
	 * <p>
	 * As the name implies the transaction is already processed, in the sense
	 * that it has already being accepted. The validation part has to be yet
	 * thought of, but I suppose that it can be done before.
	 * 
	 * @param isLong
	 * @param aTransaction
	 * @throws TEAException
	 */
	public void newTransaction(boolean isLong, Transaction aTransaction) {

		if (!_stockInfo.checkPriceCoherence(aTransaction._singlePrice)) {
			throw new IllegalArgumentException("Invalid price "
					+ aTransaction._singlePrice);
		}
		if (isLong) {
			_longInventory.newTransaction(aTransaction);
		} else {
			_shortInventory.newTransaction(aTransaction);
		}
	}

	/**
	 * The quantity held by a duplex inventory is the sum of the long and the
	 * short quantities together.
	 * 
	 * @return the overall quantity.
	 */
	@Override
	public int getQuantity() {
		return _longInventory.getQuantity() + _shortInventory.getQuantity();
	}

	/**
	 * Also the total cost is simply the sum of the long and the short costs
	 * together.
	 * 
	 * <p>
	 * The cost of the short inventory is always negative, because it is
	 * actually an anticipated gain.
	 * 
	 * @return the total cost.
	 */
	@Override
	public long getTotalCost() {
		return _longInventory.getTotalCost() + _shortInventory.getTotalCost();
	}

	/**
	 * Return the imaginary profit (or loss) that could be made if we sold
	 * (bought) entirely the inventory, going to a quantity zero.
	 * 
	 * 
	 * @param aMarketPrice
	 *            the market price used to compute the imaginary profit or loss.
	 * @return a profit (if positive) or a loss (if negative)
	 */
	@Override
	public long getUnrealizedPL(int aMarketPrice) {

		// long totalValue = _stockInfo.convertToPrice(aMarketPrice
		// * getQuantity());
		// return totalValue - getTotalCost();
		return _longInventory.getUnrealizedPL(aMarketPrice)
				+ _shortInventory.getUnrealizedPL(aMarketPrice);

	}

	@SuppressWarnings("boxing")
	public void _testDump() {
		U.debug_var(193103, this, " eq. ", this._equity.getEquity());
		this._longInventory._testDump();
		this._shortInventory._testDump();
	}

}
