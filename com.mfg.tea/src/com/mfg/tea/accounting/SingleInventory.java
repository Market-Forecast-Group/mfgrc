package com.mfg.tea.accounting;

import java.util.ArrayDeque;
import java.util.Deque;

import com.mfg.common.TEAException;
import com.mfg.tea.conn.IAccountStatisticsMoney;
import com.mfg.utils.U;

/**
 * A single inventory models a virtual warehouse that can stock only one kind of
 * item and tracks its total value, the total quantity, etc...
 * 
 * <p>
 * There are different ways to track an inventory, like <b>FIFO</b>,
 * <b>LIFO</b>, and <b>average cost</b>, they have different uses, for now we
 * simply use the FIFO method, which is simpler and has a better physical
 * meaning (because old objects are bought/sold) before.
 * 
 * <p>
 * There is a caveat, though, the simple inventory can track also negative
 * quantities, in that case, instead of a total cost of an inventory we have a
 * "potential" gain of the inventory, potential because we are shorting an item.
 * 
 * <p>
 * The total value of the inventory is simply the algebraic sum of the two
 * simple inventories.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class SingleInventory implements IBasicInventory {

	// /**
	// * A single inventory is tied to a partition of an execution log. A
	// * partition because the inventory is only long or short.
	// */
	// ExecutionLog _log;

	/**
	 * TEA is able to handle the private accounting in different ways.
	 * 
	 * <p>
	 * The standard ways are fifo, lifo and avco.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	enum InventoryAccountingMode {
		FIFO, LIFO, AVERAGE_COST
	}

	/**
	 * This is the accounting mode shared by all the {@link DuplexInventory}
	 * classes. At one time it will be modifiable but for now it is not.
	 * 
	 * <p>
	 * Probably the fifo is the simplest and most directly physical than the
	 * other two so we start with it.
	 * 
	 */
	final static InventoryAccountingMode _mode = InventoryAccountingMode.FIFO;

	/*
	 * Every "simple" inventory is associated to a simple equity account which
	 * is able to track all the financial computations.
	 * 
	 * Of course we are in a double entry system, so a credit (adding) in stock
	 * is a debit in equity, viceversa for a debit (which will be a credit for
	 * the equity).
	 * 
	 * In a certain sense the equity is one thing... it is the movement of the
	 * assets which is important for the application.
	 * 
	 * If I buy 100$ of apples my total assets is not modified as long as I do
	 * not eat them.
	 * 
	 * The difference is that we may track the variations of the equity based on
	 * a market price.
	 */

	/**
	 * An inventory partition is a partition of an inventory of items bought at
	 * the same time and at the same price.
	 * 
	 * <p>
	 * As soon as the inventory is depleted we start to deplete the oldest
	 * partitions (fifo), the newest (lifo) or a combination...
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	static class InventoryPartition {

		/**
		 * Builds a chunk for an inventory, that is a determined amount of goods
		 * which have been purchased (or sold) at a single price.
		 * 
		 * <p>
		 * The quantity may be negative but <b>only</b> in a negative inventory
		 * (a short inventory).
		 * 
		 * @param aQuantity
		 *            the quantity bought or sold (if negative).
		 * @param aPrice
		 */
		InventoryPartition(int aQuantity, int aPrice) {
			quantityLeft = aQuantity;
			priceOfPurchase = aPrice;
		}

		/**
		 * The quantity left in this partition, when a partition is finished it
		 * is removed from the inventory.
		 * 
		 * <p>
		 * The quantity may be negative. In a negative simple inventory the
		 * quantities are reversed.
		 */
		int quantityLeft;
		/**
		 * The price of purchase is the price @ which the items in this
		 * inventory have been bought.
		 */
		int priceOfPurchase;
	}

	/**
	 * All the partitions are in a double ended queue to support the removal
	 * from the front as we need to remove the oldest partitions which are not
	 * any more used.
	 * 
	 * <p>
	 * Only a real inventory has the array of partitions. The <i>parent</i>
	 * inventories derive their quantity from the quantity of the children.
	 * 
	 * <p>
	 * But the parent inventory is only able to know the amount of stock
	 * contained in the global warehouse, and it also has a notion of a total
	 * cost and a realized profit and loss for this stock.
	 * 
	 * <p>
	 * A parent inventory <b>cannot</b> have a transaction because the
	 * transaction would span different inventories and this is not allowed,
	 * that is we cannot have a transaction that spans different virtual
	 * brokers, it wont' have sense.
	 */
	Deque<InventoryPartition> _partitions = new ArrayDeque<>();

	/**
	 * This is the equity object which holds the profit and losses accumulated
	 * by this inventory.
	 * 
	 * <p>
	 * It is a different object because it can hold the parent equity, the one
	 * which holds the accumulated profits and losses (in money) of all the
	 * objects of a single agent.
	 */
	final SimpleEquity _equity;

	/**
	 * <p>
	 * This is the cost of inventory... the historical cost. This quantity is
	 * history related.
	 * <p>
	 * It is updated every time a new purchase or a sell is done.
	 * 
	 * <p>
	 * The value of inventory is always positive (or zero) for long accounts and
	 * negative (or zero) for short accounts.
	 * 
	 * <p>
	 * The net value for the duplex (complete) stock is the algrebric sum of the
	 * long and short.
	 * 
	 * <p>
	 * The cost of inventory is a double because it can be a fractional amount,
	 * if we use the average cost to compute it.
	 * 
	 * <p>
	 * This is the total cost of this simple inventory for a long inventory...
	 * or a total "gain" if it is a negative inventory (a short one). The total
	 * cost is dependent on the type of computation (fifo, lifo or averaged).
	 * 
	 * <p>
	 * The cost is measured in points, not in money.
	 */
	private long _totalCost;

	/**
	 * The quantity resulted from the sum of all the transactions.
	 * 
	 * <p>
	 * This quantity has a constraint, it must ge positive or null for long and
	 * negative or null for short inventories.
	 * 
	 * <p>
	 * We <b>cannot</b> be short on a long inventory, and viceversa we cannot be
	 * <b>long</b> on a short inventory.
	 * 
	 * This is enforced by the {@link #newTransaction(Transaction)} method.
	 * 
	 * <p>
	 * The net quantity is the only field required (with the current price
	 * market, of course) to know the probable gain (or loss) of this inventory.
	 */
	private int _quantity;

	/*
	 * Is the simple inventory also responsible for a simple equity? Yes... this
	 * is the account leaf in the old terms.
	 */

	/*
	 * The simple inventory class is actually a "simple" account, where the
	 * other masked account is the equity. This is why we don't explicitly mark
	 * the other account in the transaction, because it is always the "equity"
	 * of the virtual trader.
	 * 
	 * In this sense we have an "oil" equity, a "google" equity or anything
	 * else, which can be added, because they are based on $, which are addable,
	 * even if their quantities cannot, because of course they are composed of
	 * different things.
	 */

	/**
	 * The transaction must be stored because the history of transactions
	 * determines the total cost of the inventory itself.
	 */
	// @SuppressWarnings("unused")
	// private final ArrayList<SimpleTransaction> _transactions = new
	// ArrayList<>();

	/**
	 * A negative inventory is a special inventory in which the total quantity
	 * is always negative or zero. It means that we have sold items before we
	 * own them, actually making a short sell.
	 */
	private final int _isNegative;

	/**
	 * The information about the stock info, to convert the points in prices and
	 * viceversa.
	 */
	final StockInfo _info;

	/**
	 * 
	 * @param aDuplex
	 *            the duplex inventory which contains this simple inventory. It
	 *            <b>cannot</b> be null.
	 * @param isNegative
	 *            if true this simple inventory tracks negative quantities, i.e.
	 *            it refers to a short account.
	 * 
	 * @throws NullPointerException
	 *             if aDuplex is null.
	 */
	public SingleInventory(StockInfo aInfo, boolean isNegative) {
		_isNegative = isNegative ? -1 : 1;
		// _duplex = aDuplex;
		_info = aInfo;
		_equity = new SimpleEquity(this);
	}

	/**
	 * register a new transaction for this equity. The transaction may be a
	 * "opening" transaction or a closing one, that is it tries to lower the
	 * amount of quantity held.
	 * 
	 * @param aTransaction
	 * @throws TEAException
	 */
	void newTransaction(Transaction aTransaction) {

		/*
		 * The price must be coherent with the tick size, this check is done in
		 * the duplex inventory.
		 */

		if (Math.signum(_isNegative * aTransaction._quantity) < 0) {
			/*
			 * The transaction goes "against" the account, that is we are
			 * selling if long or buying if short. Me must be sure that this
			 * won't change the overall sign of the account.
			 */

			/*
			 * Let's see if the quantity is above the total quantity
			 */
			if (Math.abs(aTransaction._quantity) > Math.abs(_quantity)) {
				throw new IllegalArgumentException(
						"The account cannot change sign ("
								+ aTransaction._quantity + ") > abs("
								+ _quantity + ")");
			}

			// Ok, this is good, I do not exceed the total quantity

			/*
			 * But the total quantity must be updated!
			 */
			_quantity += aTransaction._quantity;

			switch (_mode) {
			case AVERAGE_COST:
				assert (false); // todo
				break;
			case FIFO:
				_depleteStocksFifo(aTransaction);
				break;
			case LIFO:
				assert (false); // to do
				break;
			default:
				throw new IllegalStateException(
						"unknown mode to compute the inventory" + _mode);

			}

		} else {
			/*
			 * We are simply adding stocks, that is becoming shorter or longer
			 * from our current position. Nothing to check, at least here.
			 * Somewhere else we must have checked if this transaction is
			 * accepted or not.
			 */

			_quantity += aTransaction._quantity;
			_totalCost += aTransaction._quantity * aTransaction._singlePrice;

			_partitions.add(new InventoryPartition(aTransaction._quantity,
					aTransaction._singlePrice));

			/*
			 * As you can see the equity here is not modified. Because we have
			 * simply done an exchange. Nothing is lost (well, there are
			 * commissions... but here we are not tracking them).
			 * 
			 * 
			 * But suppose we did. In that case we would have that the equity
			 * would change because we have a commission cost.
			 * 
			 * we would have something like
			 * 
			 * _equity.new...opened...
			 * 
			 * but that would be a split transaction, something more
			 * complicated, something like:
			 * 
			 * 
			 * equity 100.10
			 * 
			 * stock 100 commissions 0.10
			 */

		}

	}

	/**
	 * Depletes (or adds in case of a short inventory) the given transaction
	 * from the amount of stocks present.
	 * 
	 * @param aTransaction
	 */
	private void _depleteStocksFifo(Transaction aTransaction) {

		/*
		 * I have to start from the oldest items
		 */
		int amountLeft = aTransaction._quantity;
		int partitionsRemoved = 0;
		for (InventoryPartition partition : _partitions) {
			if (Math.abs(amountLeft) > Math.abs(partition.quantityLeft)) {
				/*
				 * This partition is to be removed, and it serves to update the
				 * amount left.
				 */
				partitionsRemoved++;
				amountLeft += partition.quantityLeft;

				/*
				 * I can also update the total cost, this is an algebric sum, so
				 * it may be an addiction or a subtraction.
				 */
				_totalCost -= partition.quantityLeft
						* partition.priceOfPurchase;

				int delta = partition.quantityLeft
						* (aTransaction._singlePrice - partition.priceOfPurchase);

				_registerDeltaPoints(aTransaction._dateTime, delta);

			} else {
				partition.quantityLeft += amountLeft;

				/*
				 * I attribute the gain of this sell to the oldest price of
				 * purchase (I am doing the FIFO) algorithm.
				 */
				_totalCost -= -amountLeft * partition.priceOfPurchase;

				int delta = -amountLeft
						* (aTransaction._singlePrice - partition.priceOfPurchase);

				_registerDeltaPoints(aTransaction._dateTime, delta);

				// _equity.registerDeltaPoints(aTransaction._dateTime, delta);
				break;
			}
		}

		for (int i = 0; i < partitionsRemoved; ++i) {
			_partitions.removeFirst();
		}

	}

	/**
	 * register a chunk of equity change and chain it to the root.
	 * 
	 * <p>
	 * The equity change is measured in points...
	 * 
	 * <p>
	 * But if I chain the transaction upwards then it will later be converted in
	 * money, because not all the equities share the same material, so probably
	 * it will be better to update the equity in money, and not in points.
	 * 
	 * @param _dateTime
	 *            the date time of the closed trade transaction.
	 * 
	 * @param delta
	 *            the delta in points.
	 */
	private void _registerDeltaPoints(long _dateTime, int delta) {
		_equity.registerDeltaPoints(_dateTime, delta);
	}

	public int getQuantity() {
		return _quantity;
	}

	@Override
	public long getTotalCost() {
		return _info.convertToPrice(_totalCost);
	}

	@SuppressWarnings("boxing")
	public void _testDump() {
		U.debug_var(101837, "long? ", (this._isNegative < 0 ? false : true),
				" q ", this._quantity, " eq ", this._equity.getEquity());

	}

	@Override
	public long getUnrealizedPL() {

		return 420;
	}

	@Override
	public long getUnrealizedPL(int aPrice) {
		long totalValue = this._quantity * aPrice;
		totalValue -= _totalCost;
		// now convert it to money
		return _info.convertToPrice(totalValue);
	}

	@Override
	public double getAverageCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IAccountStatisticsMoney getStats() {
		return _equity;
	}

}