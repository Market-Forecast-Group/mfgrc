package com.mfg.dfs.conn;

import com.mfg.dfs.misc.IDataFeedListener;
import com.mfg.utils.IMarketConnectionStatusListener;

/**
 * This is the client interface for the DFS session.
 * 
 * <p>
 * Each client that connects to
 * {@linkplain IDFS#createSession(IDFSListener, String, String, Object)} method
 * must implement this interface.
 * 
 * <p>
 * All methods should return as quickly as possible, as the server calls them in
 * the same thread as (maybe) other sessions.
 * 
 * <p>
 * There is <b>no way</b> (deliberately) to have incomplete bars. When you ask
 * for bars you get only complete bars up to the most recent interval. Usually
 * the last bar is incomplete in the data provider, but we don't give it.
 * 
 * <p>
 * This interface is almost identical to the {@linkplain IDataFeedListener}, the
 * only difference is that the {@link #onNewQuote(String, long, String)} method
 * will now get the price as a long.
 * 
 * <p>
 * Probably the two interfaces could be brought together, but for now they are
 * separated.
 * 
 * @author Sergio
 * 
 */
public interface IDFSListener extends IMarketConnectionStatusListener/*
																	 * ,
																	 * IDFSQuoteListener
																	 */{

	//

}
