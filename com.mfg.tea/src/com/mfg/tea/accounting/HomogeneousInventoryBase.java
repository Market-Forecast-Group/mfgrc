package com.mfg.tea.accounting;

import com.mfg.dfs.misc.VirtualSymbolBase;
import com.mfg.dm.MfgDataSource;
import com.mfg.tea.conn.IDuplexStatistics;

/**
 * The base class for all the inventories which track only one material.
 * 
 * <p>
 * All the accounts that share the same symbol compose a tree of which this
 * class is a middle object, or the root itself.
 * 
 * <p>
 * This tree is a homogeneous tree, all the folder or inventories here share the
 * same inventory. There may be separate inventories tree with the same stock.
 * But in this tree there must <b>not</b> be a {@link MixedInventoriesFolder}
 * object which may be a parent of this tree, however.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class HomogeneousInventoryBase extends AbstractInventoryItem {

	@SuppressWarnings("unused")
	private int _lastPrice;

	/**
	 * The stock info is of course related to the trading symbol. I repeat here
	 * the information but may be not necessary.
	 * 
	 * 
	 */
	final StockInfo _stockInfo;

	/**
	 * builds a duplex inventory which is used to act as a base (a root or a
	 * not-leaf) item in a tree where all the inventories share the same stock.
	 * 
	 * test
	 * 
	 * @param aParent
	 *            the parent to set.
	 * 
	 * @param aMixParent
	 * 
	 *            the mixed parent. Here we are in the
	 * 
	 */
	protected HomogeneousInventoryBase(DuplexEquityBase aEquityBase,
			MixedInventoriesFolder aMixParent, StockInfo aInfo) {
		super(aEquityBase, aMixParent, aInfo, aInfo.stockName);
		_stockInfo = aInfo;
	}

	@Override
	public double getAverageCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		return _stockInfo.stockName;
	}

	/**
	 * Only a homogeneous inventory can have a quantity. A mixed folder may have
	 * only a set of quantities.
	 * 
	 * @return the quantity of this folder.
	 */
	public abstract int getQuantity();

	/**
	 * Note the different return value. In this class we have only homogeneus
	 * inventories, so the statistics are homogeneus and we return the expanded
	 * stats, the one which have also ticks and points as units.
	 */
	@Override
	public IDuplexStatistics getStats() {
		/*
		 * This cast is safe, because derived classes create only homogeneous
		 * equities.
		 */
		return (IDuplexStatistics) _equity;
	}

	@Override
	public long getUnrealizedPL() {
		return 342;
	}

	//

	@Override
	public long getUnrealizedPL(int aPrice) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Receives a new stock price.
	 * 
	 * <p>
	 * The new stock price is used as a basis for the open trade equity. For now
	 * there is not an history of prices, because we are only interested in the
	 * current value of the equity.
	 * 
	 * <p>
	 * The current price is not chained upwards or downwards, because we do not
	 * need that, at least now. Every {@link VirtualSymbolBase} subscribes to a
	 * {@link MfgDataSource} which gives the quotes, so this is not really
	 * necessary. Eventually we may have a central point of updating.
	 * 
	 * 
	 * @param aTime
	 *            for now it is unused.
	 */
	public final void onNewStockPrice(long aTime, int price) {
		_lastPrice = price;
		// ((DuplexEquity) _equity).changedPrice(price);
	}

}
