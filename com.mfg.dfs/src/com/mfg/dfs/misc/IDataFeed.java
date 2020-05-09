package com.mfg.dfs.misc;

import com.mfg.common.DFSException;
import com.mfg.dfs.data.HistoryRequest;
import com.mfg.dfs.data.HistoryTable;
import com.mfg.dm.IDataProvider;

/**
 * The data feed is the base class for all the data feed that can be.
 * 
 * <p>
 * It is roughly similar to the {@linkplain IDataProvider} interface, but this
 * is raw and should be generic enough to be adapted to different data feeds in
 * the future.
 * 
 * <p>
 * The data feed is coupled with the {@linkplain HistoryTable} because the
 * history table is the way in which the DFS will then serve the data to the
 * views.
 * 
 * <p>
 * The data feed has <b>NOT</b> a link to the view. The views do not use the
 * data feed to get the data, they only refer to the {@linkplain HistoryTable}
 * class.
 * 
 * <p>
 * It <b>must</b> be perfectly safe to have two requests of the same symbol and
 * the same interval; they will be given different handles and they will get the
 * bars at the same time (even if the indeces may be different).
 * 
 * <p>
 * The data will come through the {@linkplain IDataFeedListener} interface,
 * using the data feed thread.
 * 
 * @author Sergio
 * 
 */
public interface IDataFeed {

	/**
	 * creates a "watch" on the given symbol.
	 * 
	 * <p>
	 * The watch is a subscription in the traditional sense (even if iqFeed
	 * calls it a "watch").
	 * 
	 * <p>
	 * The symbol here is a real symbol, it <em>cannot</em> be a continuous
	 * symbol because the dfs does not know how to handle this.
	 * 
	 * 
	 * @param symbol
	 * @throws DFSException
	 */
	public void subscribeToSymbol(String symbol) throws DFSException;

	public void unsubscribeSymbol(String symbol) throws DFSException;

	/**
	 * This method starts the data feed in <b>its own thread</b>.
	 * <p>
	 * As the market is asynchronous every data feed is in some way independent
	 * and must react asynchronously to the events from the market.
	 * 
	 * <p>
	 * This is one of the three threads in the system DFS. The other two are the
	 * thread which periodically pools the history tables and the views (to give
	 * to the clients the bars they have requested) and the other one (optional)
	 * is the thread which waits for the socket connections (it is present only
	 * if we start the server remotely).
	 * 
	 * <p>
	 * every data feed could have some properties regarding the particular
	 * connection to the real data feed, for example user, password, etc.
	 * 
	 * @param connectionString
	 *            the string which is used to connect to the real data feed,
	 *            treat it like a JSON object
	 * @throws DFSException
	 */
	public void start(String connectionString) throws DFSException;

	/**
	 * stops the data feed.
	 * <p>
	 * It stops all the requests, all the subscriptions. The data feed can be
	 * restarted using the {@link #start(String)} method again.
	 */
	public void stop();

	/**
	 * The data feed is internal in the DFS and it is not used from the external
	 * world, even when the service is local and not remote.
	 * <p>
	 * Even in that case, in fact, the data is first stored in the cache and
	 * then given to the outside in the form of a View.
	 * 
	 * <p>
	 * The data feed implementation is responsible for giving the correct bars,
	 * orders, etc... the only difference is that we are responsible for
	 * catching up data from the last feed time stamp.
	 * 
	 * <p>
	 * The request is <b>always</b> blocking. Asynchronous data feed are then
	 * blocked until all the data has arrived (or an error comes).
	 * 
	 * <p>
	 * eSignal used to have a "continue in real time" flag, but this is not so
	 * important now because we have now iqFeed which does not have this flag.
	 * 
	 * @param aRequest
	 *            the request which you want to add. The request has a reference
	 *            to the table and it does not need a custom listener.
	 * 
	 * 
	 */
	public void requestHistory(HistoryRequest aRequest) throws DFSException;

	/**
	 * 
	 * @return true if it is connected
	 */
	public boolean isConnected();

}
