package com.mfg.tea.conn;

/**
 * The base interface for all the statistics double with only money as the unit
 * measure.
 * 
 * <p>
 * the object itself is a equity holder.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDuplexStatisticsMoney extends IAccountStatisticsMoney {
	/**
	 * @return the long statistics.
	 * 
	 */
	IAccountStatisticsMoney getLongStatistics();

	/**
	 * 
	 * @return the short statistics.
	 */
	IAccountStatisticsMoney getShortStatistics();

}
