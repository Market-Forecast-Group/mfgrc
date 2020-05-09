package com.mfg.dfs.misc;

import com.mfg.utils.IMarketConnectionStatusListener;

/**
 * This is the listener of the data feed.
 * 
 * <p>
 * The {@linkplain Service} is for now the unique implementor of this interface.
 * I have created it only for symmetry with the {@linkplain IDataFeed}
 * interface.
 * 
 * <p>
 * In reality the data feed is also called by the history tables, these use the
 * data feed to fill the data initially.
 * 
 * @author Sergio
 * 
 */
public interface IDataFeedListener extends IMarketConnectionStatusListener {

	/**
	 * called when a new quote of the given symbol arises from the data feed.
	 * <p>
	 * The data feed will always have only one subscriber for each symbol. The
	 * multiserver will then dispatch the quote to the other clients.
	 * 
	 * <p>
	 * The quote could also be delayed; this means that its timestamp is in the
	 * past because we have requested to catch up the quotes. In any case
	 * usually the data feed will try its best not to give time gaps to the
	 * application.
	 * 
	 * <p>
	 * The symbol here is the real symbol, which may be different from the
	 * symbol which has been subscribed to, for example for the case of the
	 * continuous contract the symbol subscribed is #mfg but the real symbol is
	 * another
	 * 
	 * @param symbol
	 *            the real symbol which is seen by the data feed
	 * 
	 * @param timestamp
	 *            the time (remote in the feed server) of this quote.
	 * @param timeStampLocal
	 *            the time (local in this computer) when the quote has been
	 *            received.
	 * @param quote
	 * 
	 * @param volume
	 *            the volume which is received for this particular price.
	 */
	public void onNewQuote(String symbol, long timestamp, long timeStampLocal,
			String quote, int volume);

}
