package com.mfg.dfs.conn;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.IBarCache;
import com.mfg.common.ISymbolListener;
import com.mfg.common.IDataSource;
import com.mfg.common.Maturity;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.misc.MultiServer;
import com.mfg.dfs.misc.VirtualSymbol;
import com.mfg.dm.TickDataRequest;

/**
 * The dfs is the data provider of the mfg project.
 * 
 * <p>
 * The data can come in two distinct methods: online and offline. The online
 * data comes in the form of a quote, the offline data come in the form of a
 * bar.
 * 
 * <p>
 * Some data providers are able to mix offline and online quotes, for example
 * eSignal has an interface which gives to the application the last bar in real
 * time, with the price (close) which moves.
 * 
 * <p>
 * The online interface is push like, because prices can come asynchronously.
 * 
 * <p>
 * The offline interface is instead "query" based, because the bars are already
 * stored in the database.
 * 
 * <p>
 * 2013/05/20 in reality there is no distinction between the online interface
 * and the offline interface. There is only difference between the quote
 * subscription and the request bars.
 * 
 * <p>
 * There is also a sort of <b>booking</b> interface, which is asynchronously
 * driven, in which the app requests a certain amount of historical data which
 * is not yet present (but it may be present in the future) and it waits for the
 * data to come.
 * 
 * <p>
 * The server is thread safe, all methods to the server are synchronized.
 * 
 * @author Sergio
 * 
 */
public interface IDFS {

	/**
	 * Adds a global hook to the quotes.
	 * 
	 * <p>
	 * A global hook is an object that will be notified whenever a real quote is
	 * received in DFS, from whatever symbol and for whatever client.
	 * 
	 * <p>
	 * The global hook can be added only in the process space where there is the
	 * {@link MultiServer} object, that is either in a DFS application or in a
	 * MFG application with DFS embedded.
	 * 
	 * <p>
	 * The global hook should do its job as quickly as possible, as the method
	 * is call synchronously
	 * 
	 * 
	 * 
	 * @param aHook
	 * @return
	 */
	public boolean addGlobalQuoteHook(IQuoteHook aHook);

	/**
	 * 
	 * adds a listener to the DFS server. the difference between a
	 * {@link IDFSListener} and a {@link IDFSObserver} is that the first is a
	 * listener to events external of the DFS control, for example a new quote
	 * or a data feed lost link.
	 * 
	 * <p>
	 * The latter is instead an observer of DFS related things. This is why a
	 * listener can listen to a remote DFS and instead an observer is only
	 * local.
	 * 
	 * <p>
	 * It should be possible to add a listener also for a remote server.
	 * 
	 * @param aListener
	 *            the listener to be added.
	 * 
	 */
	public void addListener(IDFSListener aListener);

	/**
	 * 
	 * 
	 * 
	 * @param anObserver
	 *            the observer to be added.
	 * 
	 * @return true if successful
	 */
	public boolean addObserver(IDFSObserver anObserver);

	/**
	 * connnects to an existing data source.
	 * 
	 * <p>
	 * The data source must be existing on the server, the {@link MultiServer}.
	 * 
	 * 
	 * 
	 * @param dataSourceId
	 * @param aListener
	 * @throws DFSException
	 */
	public void connectToExistingDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException;

	/**
	 * creates a data source from a data request.
	 * 
	 * <p>
	 * This data request may have different layers, based on the tick data
	 * request object.
	 * 
	 * <p>
	 * The returned object is created client side. A server's side object is
	 * called {@link VirtualSymbol} and is being created on result of this.
	 * 
	 * 
	 * @param request
	 *            the request which has all the information to define a virtual
	 *            symbol
	 * 
	 * @param aListener
	 *            the listener for this expansion. The listener will be
	 *            in-process because here we are talking either to the real
	 *            service or to the {@link DfsProxy} which is the client side
	 *            version of the cache expander listener (the data will come
	 *            from a push sink.).
	 * 
	 * 
	 * @return a data source interface, either a local or a proxy data source
	 *         depending on the type of DFS which is.
	 * @throws DFSException
	 */
	public IDataSource createDataSource(TickDataRequest request,
			ISymbolListener aListener) throws DFSException;

	/**
	 * Disconnects the listener from an existing data source.
	 * 
	 * <p>
	 * The creator of the data source (the one which has called the
	 * {@link #createDataSource(TickDataRequest, ISymbolListener)}) should not
	 * call this, but call the {@link IDataSource#stop()} method instead.
	 * 
	 * @param dataSourceId
	 *            the id of the data source (it is simply the virtual symbol
	 *            id).
	 * @param aListener
	 *            the listener which you want to disconnect.
	 * @throws DFSException
	 */
	public void disconnectFromExistingDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException;

	/**
	 * 
	 * returns the bar count for a particular symbol.
	 * 
	 * <p>
	 * This bar count may vary during the course of the run, because DFS runs
	 * and appends bars to the cache.
	 * 
	 * @param symbol
	 *            the complete symbol (symbol + maturity)
	 * @param aType
	 *            the type of the bar requested.
	 * @param barWidth
	 *            the width of the bar, for example 5 could mean "five minutes"
	 *            or "five days".
	 * @return the bar count (current)
	 * @throws DFSException
	 *             if no symbol or no type present
	 */
	public int getBarCount(String symbol, BarType aType, int barWidth)
			throws DFSException;

	/**
	 * Query method to know the number of bars between two dates.
	 * <p>
	 * If the dates are invalid it throws an exception.
	 * 
	 * <p>
	 * The three methods
	 * {@link #getBarsBetween(String, BarType, int, long, long)},
	 * {@link #getDateAfterXBarsFrom(String, BarType, int, long, int)} and
	 * {@link #getDateBeforeXBarsFrom(String, BarType, int, long, int)} are
	 * linked in this way:
	 * 
	 * <p>
	 * For every (start,end) pair we have that
	 * <p>
	 * if <code>getBarsBetween(start,end)</code> does not throw an exception and
	 * returns X we:
	 * 
	 * <p>
	 * {@link #getDateAfterXBarsFrom(String, BarType, int, long, int)} called
	 * with start and X returns end
	 * 
	 * <p>
	 * {@link #getDateBeforeXBarsFrom(String, BarType, int, long, int)} called
	 * with end and X returns Start
	 * 
	 * @param symbol
	 *            the symbol (complete symbol - prefix + maturity -) used to
	 *            query the server.
	 * 
	 * @param aType
	 *            the type of the bar which is used to query the database
	 * 
	 * 
	 * @param barWidth
	 *            the width of the bar which is used to
	 * 
	 * @param startDate
	 *            the start date from which to start
	 * 
	 * @param endDate
	 *            the end date
	 * 
	 * @throws DFSException
	 *             if it is not possible to have the number of bars, because the
	 *             start/end time are outside the available range
	 * 
	 */
	public int getBarsBetween(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) throws DFSException;

	/**
	 * 
	 * gets the cache for a particular prefix, maturity and type of table.
	 * <P>
	 * The data should be treated read only.
	 * <p>
	 * This method is not available in the remote version of the server, if you
	 * call it on the proxy you will get a
	 * {@linkplain UnsupportedOperationException}.
	 * 
	 * <p>
	 * Well, not... in a certain sense this is the complete cache, so it could
	 * have a security block because we don't want the network to be filled with
	 * too many bars (suppose that you are asking the complete minute continuous
	 * history of ES... there are millions of rows...but this could be done
	 * server side).
	 * 
	 * @param prefixSymbol
	 * @param aMaturity
	 * @param aType
	 * @param nUnits
	 *            how many units to integrate, for example 5 means 5 minutes,
	 *            etc...
	 * @return
	 * @throws DFSException
	 */
	IBarCache getCache(String prefixSymbol, Maturity aMaturity, BarType aType,
			int nUnits) throws DFSException;

	/**
	 * returns, if possible, a datafeed controller, mainly used for GUI
	 * purposes.
	 * 
	 * <p>
	 * If the data feed allows to be remotely controllable it returns an
	 * interface for it, otherwise null.
	 * 
	 * @return the data feed controller, or null if the current data feed is not
	 *         controllable.
	 */
	public IDataFeedController getController();

	/**
	 * gets the date which is reached when numBars are added to a start date.
	 * 
	 * <p>
	 * This is the <em>end of period</em> counting <code>numBars</code> complete
	 * bars after the first bar which starts <b>not before</b> the starting
	 * date.
	 * 
	 * <p>
	 * In practice this algorithm returns the starting instant of the <b>numBars
	 * +1 (th)</b> bar.
	 * 
	 * 
	 * @param symbol
	 * @param aType
	 *            the type of the bar which is used to query the database
	 * 
	 * @param barWidth
	 *            the width of the bar
	 * @param startDate
	 * @param numBars
	 * @return the end date reached after counting numBars from the starting
	 *         date
	 * @throws DFSException
	 * 
	 */
	public long getDateAfterXBarsFrom(String symbol, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException;

	/**
	 * gets the starting date obtained subtracting numBars to the end Time for
	 * the given symbol.
	 * 
	 * <p>
	 * If there are no such bars, or no symbol or other it will throw an
	 * exception.
	 * 
	 * 
	 * @param symbol
	 * @param aType
	 *            the type of the bar which is used to query the database
	 * @param barWidth
	 *            the width of the bar used
	 * @param endTime
	 * @param numBars
	 * @return
	 * @throws DFSException
	 * 
	 * 
	 */
	public long getDateBeforeXBarsFrom(String symbol, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException;

	/**
	 * returns the scheduling times used to "wake up" the data model. *
	 * 
	 * @return a structure with all the scheduling times.
	 */
	public DfsSchedulingTimes getSchedulingTimes();

	/**
	 * gets the status for a particular status.
	 * <p>
	 * The status will list all the data which is in the particular symbol, if
	 * it is updated, how much data is present, etc.
	 * 
	 * @param symbol
	 *            the symbol should be a prefix, not a real symbol, the DFS will
	 *            return the data for the given prefix with all the maturities,
	 *            etc...
	 * @return
	 * @throws DFSException
	 */
	public DfsSymbolStatus getStatusForSymbol(String symbol)
			throws DFSException;

	/**
	 * returns the list of prefixes which are inside the dfs.
	 * 
	 * @return
	 * @throws DFSException
	 */
	public DfsSymbolList getSymbolsList() throws DFSException;
	
	/**
	 * returns true if we have connected to a simulated data feed. If we are
	 * remote there is no way to know if the remote DFS is connected to a
	 * simulated data feed and this is the only way to know it.
	 * 
	 * <p>
	 * This method is only used inside TEA and probably it may be deprecated,
	 * because TEA should be able to handle itself without knowing this.
	 * 
	 * @return
	 * @throws DFSException
	 */
	public boolean isConnectedToSimulatedDataFeed() throws DFSException;

	/**
	 * returns true if the internal scheduler inside DFS is running.
	 * 
	 * <p>
	 * This means that the observer has already received a
	 * {@link IDFSObserver#onSchedulerStartRunning()} call.
	 * 
	 * @return true if the scheduler is running.
	 */
	public boolean isSchedulerRunning();

	/**
	 * logins to the dfs server.
	 * <p>
	 * the function does return zero on success.
	 * <p>
	 * The Dfs could also not require an authentication so it is not mandatory
	 * to call this function. But if the authentication is mandatory then to
	 * call other methods will simply throw an exception saying that you must
	 * first authenticate.
	 * 
	 * @param user
	 *            The user
	 * @param password
	 *            the password
	 * @return 0 if all ok. something different from zero if user/password are
	 *         not valid.
	 * 
	 * @throws DFSException
	 *             for any other error
	 */
	public void login(String user, String password) throws DFSException;

	/**
	 * This method forces the manual scheduling in the service.
	 */
	public void manualScheduling();

	/**
	 * removes the given hook from the server.
	 * <p>
	 * 
	 * 
	 * @param aHook
	 *            the hook which is to be removed.
	 */
	public void removeGlobalQuoteHook(IQuoteHook aHook);

	/**
	 * removes a listener from the queue.
	 */
	public void removeListener(IDFSListener aListener);

	/**
	 * removes the given observer from the list of observers.
	 * 
	 * @param anObserver
	 *            the observer to be removed.
	 * 
	 * @return true if the observer has been actually removed, it was present in
	 *         the list.
	 */
	public boolean removeObserver(IDFSObserver anObserver);

	// /**
	// * This is the "catch all" request history. Instead of having thousands of
	// * request types I have preferred to have only one request which is used
	// in
	// * the system.
	// * <p>
	// * This request is <b>blocking</b>, if you need to cancel it for whatever
	// * reasons you may call (to be defined).
	// *
	// * <p>
	// * The bar cache returned is either a direct link to the mdb structures or
	// a
	// * local copy of the cache made from the proxy.
	// *
	// *
	// * @param aReq
	// * @param aMonitor
	// * a monitor, to be defined here, it will have also an
	// * asynchronous interface useful to have a progress of the
	// * receiving of the bars.
	// * @return
	// * @throws DFSException
	// */
	// public IBarCache requestHistory(RequestParams aReq, IBarCacheAsync
	// aMonitor)
	// throws DFSException;

	/**
	 * sets the scheduling times. This will replace all the scheduling times
	 * which are used in the {@linkplain DfsCacheRepo}
	 * 
	 * @param aSchedulingTimes
	 * @throws DFSException
	 */
	public void setSchedulingTimes(DfsSchedulingTimes aSchedulingTimes)
			throws DFSException;

	/**
	 * starts the server.
	 * 
	 * <p>
	 * In case of local server the start will begin the DataModel thread and the
	 * DataFeed thread.
	 * <p>
	 * For other cases the start could be a no op, because the server could
	 * already been started.
	 * 
	 * @throws DFSException
	 * 
	 */
	public void start(IDFSListener aListener) throws DFSException;

	public void stop();

	// /**
	// * Subscribe to the quote for the symbol. The quotes will be dispatched
	// * asynchronously using the listener interface.
	// *
	// * @param symbol
	// * The symbol. It is safe to subscribe more than one time to the
	// * same symbol. but please note that the symbol will not be
	// * released by the system until an equal number of calls to the
	// * unsubscribeQuote method will be done.
	// * @throws DFSException
	// * if the symbol is not valid and/or if I cannot have a tick
	// * size for it
	// *
	// */
	// public void subscribeQuote(String symbol) throws DFSException;

	/**
	 * truncates the given (complete symbol) at a specified new lenght.
	 * DESTRUCTIVE.
	 * 
	 * @param aPassword
	 * @param aSymbol
	 * @param aType
	 * @param truncateDate
	 *            the date at which you want to truncate the data.
	 * @throws DFSException
	 */
	public void truncateMaturity(String aPassword, String aSymbol,
			BarType aType, long truncateDate) throws DFSException;

	/**
	 * Unsubscribes to the given symbol.
	 * <p>
	 * The number of calls to this method should be balanced by a
	 * {@link #subscribeQuote(String)} call.
	 * <p>
	 * Calling unsubscribe more than that is an error and an exception will be
	 * thrown.
	 * 
	 * @param symbol
	 *            the symbol which you want to unsubscribe.
	 * 
	 * @throws DFSException
	 *             if the symbol is already unsubscribed (or has not been
	 *             subscribed at all!)
	 */
	public void unsubscribeQuote(String symbol) throws DFSException;

	/**
	 * Unwatches the given DB symbol.
	 * 
	 * <p>
	 * It is an error to unwatch a symbol which is not currently watched.
	 * 
	 * @param aSymbol
	 * @throws DFSException
	 */
	public void unWatchDbSymbol(IDatabaseChangeListener aListener,
			String aSymbol) throws DFSException;

	/**
	 * Watches the given symbol in the database. This is different from the
	 * method {@link #subscribeQuote(String)} because that method gives to us
	 * the quotes, instead this method will notify the listener when the status
	 * of this particular symbol (Prefix + maturity) has changed.
	 * 
	 * <p>
	 * Calling this method twice for the same symbol has no effect, in
	 * particular if you call this method with a different listener the new
	 * listener will overwrite the old one, and the old listener will not
	 * receive any more notifications.
	 * 
	 * @param symbol
	 *            the <b>complete</b> symbol name, for example "QGCU14". It may
	 *            be a continuous symbol, that is a prefix with a suffix in
	 *            "#mfg".
	 * @throws DFSException
	 *             if the symbol is not existent.
	 */
	public void watchDbSymbol(IDatabaseChangeListener aListener, String symbol)
			throws DFSException;

}
