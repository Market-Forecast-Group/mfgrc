package com.mfg.tea.accounting;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The inventory has a history made of transactions.
 * 
 * <p>
 * Each transaction is linked to an order in trading terms, and this
 * correspondence is one to many, because possibly an order may give rise to a
 * list of transaction at different rates (or times, or both).
 * 
 * <p>
 * This may change in the future as we may have partial fills of orders, so an
 * order may generate a series of transactions.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class Transaction {

	private static final AtomicInteger _nextId = new AtomicInteger();

	/**
	 * 
	 * The quantity may be negative. It means simply that we sell things.
	 * 
	 * @param aQuantity
	 * @param aSinglePrice
	 * 
	 * @throws IllegalArgumentException
	 *             if quantity is zero or if price is zero or negative.
	 */
	public Transaction(int aQuantity, int aSinglePrice) {
		this(System.currentTimeMillis(), aQuantity, aSinglePrice);
	}

	public Transaction(long aExecTime, int aQuantity, int aSinglePrice) {
		if (aQuantity == 0 || aSinglePrice <= 0) {
			throw new IllegalArgumentException();
		}

		_id = _nextId.incrementAndGet();
		_dateTime = aExecTime;
		_quantity = aQuantity;
		_singlePrice = aSinglePrice;
	}

	/**
	 * The usual time stamp of this transaction. As always all the times
	 * internal in the application are described in UTC time zone.
	 */
	long _dateTime;

	/**
	 * The id of the transaction. This is linked to the order id. The order id
	 * is not unique across the runs so this means that we cannot store a
	 * historical view of all the transactions in the system...
	 * 
	 * <p>
	 * The real unique primary key is the virtual broker id, which is like an
	 * execution id.
	 * 
	 * <p>
	 * Remember that a virtual broker is able only to trade a symbol, this also
	 * in the real case, because it is attached, subscribed to a real symbol (to
	 * compute the open equity).
	 */
	int _id;

	/**
	 * The quantity bought (positive) or sold (negative).
	 * <p>
	 * Each transaction can buy something or sell something from an account to
	 * the external world. But the problem is that we have two kinds of
	 * accounts, a positive (long) account and a negative (short) account. So a
	 * negative transaction in a negative account is actually a positive
	 * movement.
	 * 
	 * <p>
	 * This change in sign is not relevant here, the transaction does not know
	 * the account and the good used. It simply registers a movement.
	 * 
	 * <p>
	 * This is the <b>initial quantity of the stock, this initial quantity may
	 * be then partially or totally obliterated by the subsequent
	 * transactions.</b>
	 * 
	 * Maybe we could divide the buys from the sells...
	 */
	int _quantity;

	/**
	 * This is the single price used to buy or sell an item. It is described in
	 * dollar terms or by a number, which is the "price" at which the commodity
	 * is bought or sold. For example the @ES index has not a price, but a
	 * number, the single price is that number.
	 * 
	 * <p>
	 * That single price can then be converted in a currency price by the
	 * {@link StockInfo} information which is stored in the
	 * {@link DuplexInventory}.
	 * 
	 * <p>
	 * If the transactions involves different buys at different rates it must be
	 * split in different transactions at the same rate.
	 */
	int _singlePrice;

}
