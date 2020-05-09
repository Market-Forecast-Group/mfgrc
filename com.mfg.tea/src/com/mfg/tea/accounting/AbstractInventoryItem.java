package com.mfg.tea.accounting;

import com.mfg.tea.conn.IDuplexStatisticsMoney;

/**
 * This is the base class for all the inventories.
 * 
 * <p>
 * A basic inventory item is something which is used to
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class AbstractInventoryItem implements IInventory {

	public IDuplexStatisticsMoney getTotalStatistics() {
		return _equity;
	}

	@Override
	public long getTotalCost() {
		return 99;
	}

	/**
	 * The mixed parent for this base. If a mix parent is given this object will
	 * take the profit an losses of this inventory and all of its children (if
	 * any). But the quantities will not be propagated in this object, of
	 * course, because this mixed parent could have children of different
	 * stocks.
	 */
	final MixedInventoriesFolder _mixParent;

	/**
	 * Every abstract inventory item can have a mixed parent. Only the
	 * {@link HomogeneusInventoriesFolder} or derived classes can have a
	 * homogeneous folder, though.
	 * 
	 * @param aMixParent
	 *            the mixed folder which is a parent of this abstract item.
	 * @param aName
	 *            the name for this item. It could be retrieved by
	 *            {@link #getName()}, but the fact is that the derived object
	 *            has been not created yet.
	 */
	public AbstractInventoryItem(DuplexEquityBase aEquityBase,
			MixedInventoriesFolder aMixParent, StockInfo aInfo, String aName) {
		_equity = aEquityBase;
		_mixParent = aMixParent;
		if (_mixParent != null) {
			_mixParent.addInventory(aName, this, aInfo);
		}
	}

	@Override
	public IDuplexStatisticsMoney getStats() {
		return _equity;
	}

	/**
	 * Every inventory item has the possibility to have an equity which is not a
	 * normal equity but a virtual equity made of components. The <i>real</i>
	 * equity will be inside the {@link SingleInventory} object.
	 * 
	 * <p>
	 * In reality every inventory item has <b>three</b> equities, the long, the
	 * short and the total.
	 * 
	 * <p>
	 * For the {@link DuplexInventory} the long and the short are
	 * {@link SimpleEquity}, for the others, also the long and the short ones
	 * are {@link EquityMerger} objects.
	 * 
	 * <p>
	 * All the equities inside the inventory are duplex...
	 * 
	 */
	final DuplexEquityBase _equity;

}
