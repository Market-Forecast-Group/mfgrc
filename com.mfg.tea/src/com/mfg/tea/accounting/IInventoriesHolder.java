package com.mfg.tea.accounting;

import java.util.List;

import com.mfg.tea.conn.IDuplexStatisticsMoney;

/**
 * an interface which models an object which is used to hold different
 * inventories.
 * 
 * <p>
 * The inventory holder may (or may not) sum all the inventories held together
 * in a single equity.
 * 
 * <p>
 * This depends on the fact that some inventories are "logically" summable (for
 * example the folder that holds all the real trading symbols), but some folders
 * are only logical grouping of unrelated inventories and they cannot be
 * logically grouped.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IInventoriesHolder {

	/**
	 * All the objects which can contain inventories have a name.
	 * 
	 * <p>
	 * The name is different in the case it is a homogeneous item or a mixed
	 * item. In the second case it is a string given from the outside, in the
	 * first case usually the name is the name of the stock info for this item
	 * 
	 */
	public String getName();

	/**
	 * @return the list of symbols held by the
	 */
	public List<String> getListOfHeldSymbols();

	/**
	 * returns the statistics for a given symbol.
	 * 
	 * <p>
	 * The holder itself may not have statistics on its own, because it may be a
	 * phony holder of other inventories, just a "logical" folder.
	 * 
	 * @param aSymbol
	 * @return
	 */
	public IDuplexStatisticsMoney getStatsForSymbol(String aSymbol);

	/**
	 * Gets the total statistics for this folder. They may not exist, in this
	 * case a null is returned to signify that this holder is just a logical
	 * holder.
	 * 
	 * @return the total statistics for this holder, null if this holder does
	 *         not allow the totalization of the statistics themselves.
	 */
	public IDuplexStatisticsMoney getTotalStatistics();

}
