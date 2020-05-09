package com.mfg.dfs.conn;

/**
 * 
 * A one-method interface which models a quote watcher, a "hook" which will be
 * called whenever a new quote arrives in DFS, for whatever reason.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IQuoteHook {
	/**
	 * called when the raw quote from the outside is received.
	 * <p>
	 * This means that the quote is got without any processing, in fact it
	 * remains as a string.
	 * 
	 * <p>
	 * The GUI can use this quote to update some "quote watcher" independent on
	 * the trading configuration (if the GUI requires it)
	 * 
	 * @param symbol
	 *            the symbol quoted
	 * @param datetime
	 *            the datetime of the quote as received from the data feed.
	 * @param timeStampLocal
	 *            the datetime of the quote in local time
	 * @param quote
	 */
	public void onNewQuote(String symbol, long datetime, long timeStampLocal,
			String quote);
}
