package com.mfg.dfs.conn;

import com.mfg.dfs.data.MaturityStats;

/**
 * This interface is used to have notifications of a change in the database.
 * 
 * <p>
 * The interface is only used after a call to to a
 * {@link IDFS#watchDbSymbol(String)} method.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDatabaseChangeListener {

	/**
	 * 
	 * Called to notify that a new status is available for the current maturity.
	 * 
	 * <p>
	 * The given maturity has been watched after a call to
	 * {@link IDFS#watchDbSymbol(String)} method.
	 * 
	 * @param aSymbol
	 * @param newStats
	 */
	public void onSymbolChanged(String aSymbol, MaturityStats newStats);

}
