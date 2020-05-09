package com.mfg.tea.conn;

/**
 * A duplex statistics is able to give long and short "views" of itself.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDuplexStatistics extends IDuplexStatisticsMoney,
		IAccountStatistics {
	/**
	 * @return the long statistics.
	 * 
	 */
	@Override
	ISingleAccountStatistics getLongStatistics();

	/**
	 * 
	 * @return the short statistics.
	 */
	@Override
	ISingleAccountStatistics getShortStatistics();
}
