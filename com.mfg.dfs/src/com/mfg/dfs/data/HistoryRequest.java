package com.mfg.dfs.data;

import com.mfg.common.BarType;
import com.mfg.dfs.misc.IDataFeed;

/**
 * This is an helper class which is used to connect a {@linkplain HistoryTable} with a
 * {@linkplain IDataFeed}.
 * 
 * <p>The link is done in the form of a closure; this object holds the reference
 * to the history table and is able to access the "right" history table doing
 * a custom dispatching, without using a real interface.
 * 
 * <p>Here the prices are converted in the "normal" form, that is from string
 * to long, using the scale provided by the HistoryTable table.
 * 
 * @author Sergio
 *
 */
public class HistoryRequest {
	
	/**
	 * This is the reference table that has done this request.
	 */
	private final IHistoryFeedListener _listener;
	public final String _symbol;
	
	/**
	 * This is the bar type which is used to determine the type of request which
	 * I do to the data feed.
	 */
	private final BarType _barType;
	
	/**
	 * protected constructor; this is only used by descendant classes
	 * 
	 * <p>All history requests share a symbol and a listener but then are different
	 * and they have also different parameters.
	 * 
	 * <p>In former times this was an "active" class, because it had the possibility
	 * to get the bars and the bars where then given to the table.
	 * 
	 * <p>Now the class has become more or less a simple POD, because it
	 * has no logic.
	 * 
	 * @param aListener
	 */
	protected HistoryRequest(IHistoryFeedListener aListener, String aSymbol, BarType aType){
		_symbol = aSymbol;
		_listener = aListener;
		_barType = aType;
	}

	public IHistoryFeedListener getListener() {
		return _listener;
	}
	
	public BarType getType() {
		return _barType;
	}
}
