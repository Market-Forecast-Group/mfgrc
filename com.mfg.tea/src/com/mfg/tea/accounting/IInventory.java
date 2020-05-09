package com.mfg.tea.accounting;

import com.mfg.tea.conn.IDuplexStatisticsMoney;

/**
 * The public, common, interface for all the inventories.
 * 
 * <p>
 * This interface has no quantities, because this interface is used also by the
 * "mixed" inventories which have no single quantity.
 * 
 * <p>
 * In any case we could have here a query interface which lists all the stocks
 * for this inventory together with their quantity (long and short) and their
 * realized equity, to the benefit of the gui, but this may be done later.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IInventory extends IBasicInventory {

	/**
	 * Returns the total statistics in this inventory. If you want the
	 * statistics for a given symbol you have another method.
	 * 
	 * @return the total (mixed) stats for this inventory.
	 */
	@Override
	public IDuplexStatisticsMoney getStats();

}
