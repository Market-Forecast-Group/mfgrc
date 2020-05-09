package com.mfg.dfs.data;

import com.mfg.common.UnparsedBar;
import com.mfg.common.UnparsedTick;

/**
 * Interface used to have the history feed;
 * 
 * <p>
 * The data comes synchronously from the data feed.
 * <p>
 * In the past we used asynchronous data feeds but they were not reliable and
 * they complicate things enormously.
 * 
 * <p>
 * This interface is synchronous, it has the possibility to have the bars and to
 * process them, one at a time. Synchronous does <b>not</b> mean that all the
 * bars are received and <i>then</i> they are passed to the client.
 * 
 * 
 * 
 * @author Sergio
 * 
 */
public interface IHistoryFeedListener {

	public enum EEosStatus {
		/**
		 * The request has finished with some data
		 */
		ALL_OK,
		/**
		 * This code is used when the history request contains an invalid date,
		 * this is usually called by a corrupt table.
		 */
		INVALID_DATE,

		/**
		 * The feed is not connected, or the connection did shut down, no data
		 * can be received
		 */
		NOT_CONNECTED,
		/**
		 * The feed is connected but there is no data for this request
		 */
		NO_DATA, INVALID_SYMBOL, UNKNOWN_ERROR, GENERIC_ERROR
	}

	/**
	 * Called when the history request has finished.
	 * 
	 * <P>
	 * Usually this call is preceded by a call to a
	 * {@link #onNewIncompleteBar(UnparsedBar)}
	 * <p>
	 * Also iqfeed will give to us an incomplete bar, because the last bar is
	 * meant to be incomplete (the difference with eSignal is that eSignal will
	 * give to us many incomplete
	 * 
	 * @param historyRequest
	 */
	public void onEndOfStream(EEosStatus aStatus);

	/**
	 * callback used when a new complete bar comes from the data feed.
	 */
	public void onNewCompleteBar(UnparsedBar ub);

	/**
	 * called when a new incomplete bar comes from the data feed.
	 * <p>
	 * There are some data feeds (eSignal) which give incomplete bars even when
	 * we request only a particular history.
	 * 
	 * <p>
	 * Others, like iqFeed do not give incomplete bars, <b>but</b> the last bar
	 * (usually the minute bar) is incomplete, so it is given.
	 * 
	 * <p>
	 * Probably we don't need incomplete bars any more, so this method is going
	 * to be deleted.
	 * 
	 * @param ub
	 */
	public void onNewIncompleteBar(UnparsedBar ub);

	/**
	 * Certain data providers are able to give us also historical ticks.
	 * <p>
	 * From the historical ticks we are able to build the range bars.
	 * 
	 * <p>
	 * See the {@linkplain RangeHistoryTable} to see the algorithm.
	 * 
	 * @param ut
	 *            the unparsed tick used to build the range bars table.
	 */
	public void onHistoricalTick(UnparsedTick ut);

}
