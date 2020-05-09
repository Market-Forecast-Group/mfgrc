package com.mfg.dfs.misc;

import com.mfg.common.ISymbolListener;

/**
 * The interface is used to add a synchable method to the
 * {@link ISymbolListener} interface.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDFSSynchableQuoteListener extends ISymbolListener {

	/**
	 * waits the current thread until this listener has processed the given fake
	 * time.
	 * 
	 * @param symbol
	 *            usually (always?) this is a virtual symbol and it is the
	 *            symbol which is waited for.
	 * 
	 * @param aFakeTime
	 *            the fake time to be waited for.
	 */
	public void waitUntilProcessedTime(String symbol, int aFakeTime);

	/**
	 * tells this listener that it is going to be removed.
	 * 
	 * <p>
	 * This is valid for all the symbols in the listener, of course.
	 */
	public void aboutToBeRemoved();

}
