package com.mfg.dfs.misc;

import java.util.Date;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsInvalidRangeException;
import com.mfg.common.IBarCache;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;
import com.mfg.common.Maturity;
import com.mfg.dfs.conn.BaseService;
import com.mfg.dfs.conn.DFSDataSource;
import com.mfg.dfs.conn.DfsSchedulingTimes;
import com.mfg.dfs.conn.DfsSymbolList;
import com.mfg.dfs.conn.IDFSListener;
import com.mfg.dfs.conn.IDFSObserver;
import com.mfg.dfs.conn.IDataFeedController;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.conn.IQuoteHook;
import com.mfg.dfs.data.DataModel;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.IMarketConnectionStatusListener.EConnectionStatus;
import com.mfg.utils.IMarketConnectionStatusListener.ETypeOfData;

/**
 * The dfs main service which is always on and it updates the db.
 * 
 * <P>
 * The service talks to the connector using a socket communication
 * 
 * <P>
 * The service is always monoclient, the multiclient version of it is the
 * {@link MultiServer} class.
 * 
 * <p>
 * This class acts as the "Controller" actor in the MVC pattern.
 * 
 * <p>
 * The service has one thread of control, which is inside the data feed. The
 * Data Feed gets all the subscriptions and then it will get the "threading
 * control". This control is then used to update the history tables and
 * subscriptions.
 * 
 * <p>
 * The threading control is also used in the "local" version of the server,
 * because in any case the server starts the data feed which gets a quantum of
 * execution (a thread).
 * 
 * @author Sergio
 * 
 * 
 * 
 */
public class Service extends BaseService {

	private MultiServer _server;

	/**
	 * A service has a unique listener. The service does not implement
	 * multiclient architecture. The <i>multiclient</i> capability is made in a
	 * different part of the system, maybe using proxies, tcp/ip sockets, etc...
	 */
	private IDFSListener _listener;

	/**
	 * Creates a service; the service is created with a listener.
	 * <p>
	 * It is not possible to create a service without a listener.
	 * 
	 * @param aListener
	 *            the listener which will be forever tied to this service.
	 * @param isOffline
	 */
	public Service(MultiServer aServer) {

		_server = aServer;
		// _listener = aListener;
		//
		// // Even if I am embedded I will add a connection listener to this
		// // to know the market data status.
		// _server.addListener(aListener);
		// // the embedded server is, of course, connected.
		// aListener.onConnectionStatusUpdate(ETypeOfData.DFS_PROXY,
		// EConnectionStatus.CONNECTED);
	}

	@Override
	protected void _connectToExistingRemoteDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException {
		_server.subscribeQuote(aListener, dataSourceId);
	}

	@Override
	protected IDataSource _createDataSourceImpl(TickDataRequest aRequest,
			ISymbolListener aListener) throws DFSException {
		/*
		 * Here the server is embedded. There is a direct connection to the
		 * server and I can create the virtual symbol here, in this space.
		 * 
		 * The virtual symbol may be "remote" for other actors of the system,
		 * for example I could have DFS embedded in one client and a remote TEA
		 * which connects to this DFS in mixed mode.
		 */

		String virtualSymbol = _server.createVirtualSymbol(aRequest);

		/*
		 * I may now create a data source which is in some way tied to this
		 * virtual symbol.
		 * 
		 * The virtual symbol is independent on the data source.
		 * 
		 * The problem is that the virtual symbol is in server space and this
		 * virtual symbol can be layered, mixed, database, whatever.
		 * 
		 * For now the virtual symbol is started in another thread and will give
		 * to the outside the expanded quotes.
		 */

		return new DFSDataSource(virtualSymbol, aListener, _server,
				aRequest.getLayersSize());
	}

	@Override
	protected void _disconnectFromRemoteDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException {
		_server.unsubscribeQuote(aListener, dataSourceId);

	}

	@Override
	public boolean addGlobalQuoteHook(IQuoteHook aHook) {
		return _server.addGlobalQuoteHook(aHook);
	}

	@Override
	public void addListener(IDFSListener aListener) {
		_server.addListener(aListener);

	}

	@Override
	public boolean addObserver(IDFSObserver anObserver) {
		return _server.addObserver(anObserver);
	}

	@Override
	public int getBarCount(String symbol, BarType aType, int barWidth)
			throws DFSException {
		return _server.getBarCount(symbol, aType, barWidth);
	}

	@Override
	public int getBarsBetween(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) throws DFSException {
		if (startDate > endDate) {
			throw new DfsInvalidRangeException(
					"getBarsBetween: invalid. Start date "
							+ new Date(startDate) + " is after end date : "
							+ new Date(endDate));
		}
		return _server.getBarsBetween(symbol, aType, barWidth, startDate,
				endDate);
	}

	@Override
	public IBarCache getCache(String prefixSymbol, Maturity aMaturity,
			BarType aType, int nUnits) throws DFSException {
		return _server.getCache(prefixSymbol, aMaturity, aType, nUnits);
	}

	/**
	 * If this service is connected to the {@linkplain PseudoRandomDataFeed}
	 * than it will return a reference to it, otherwise null.
	 */
	@Override
	public IDataFeedController getController() {
		return _server.getController();
	}

	@Override
	public long getDateAfterXBarsFrom(String symbol, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		return _server.getDateAfterXBarsFrom(symbol, aType, barWidth,
				startDate, numBars);
	}

	@Override
	public long getDateBeforeXBarsFrom(String symbol, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {
		return _server.getDateBeforeXBarsFrom(symbol, aType, barWidth, endTime,
				numBars);
	}

	/**
	 * returns the data model
	 * 
	 * @return
	 */
	public DataModel getModel() {
		return _server.getModel();
	}

	@Override
	public DfsSchedulingTimes getSchedulingTimes() {
		return _server.getSchedulingTimes();
	}

	@Override
	public DfsSymbolStatus getStatusForSymbol(String symbol)
			throws DFSException {
		return _server.getStatusForSymbol(symbol, false);
	}

	@Override
	public DfsSymbolList getSymbolsList() throws DFSException {
		return _server.getSymbolsList();
	}

	@Override
	public boolean isConnectedToSimulatedDataFeed() {
		return _server.isConnectedToSimulatedDataFeed();
	}

	@Override
	public boolean isSchedulerRunning() {
		return _server.isSchedulerRunning();
	}

	@Override
	public void login(String user, String password) throws DFSException {
		// here it is a no op
	}

	@Override
	public void manualScheduling() {
		LogManager.getInstance().INFO("Booked the manual scheduling...");
		_server.manualScheduling();
	}

	@Override
	public void removeGlobalQuoteHook(IQuoteHook aHook) {
		_server.removeGlobalQuoteHook(aHook);
	}

	@Override
	public void removeListener(IDFSListener aListener) {
		_server.removeListener(aListener);
	}

	@Override
	public boolean removeObserver(IDFSObserver anObserver) {
		return _server.removeObserver(anObserver);
	}

	// @Override
	// public IBarCache requestHistory(RequestParams aReq, IBarCacheAsync
	// aMonitor)
	// throws DFSException {
	// // the monitor parameter is unused because we return immediately.
	// return _server.getCache(aReq);
	// }

	@Override
	public void setSchedulingTimes(DfsSchedulingTimes aSchedulingTimes)
			throws DFSException {
		_server.setSchedulingTimes(aSchedulingTimes);
	}

	@Override
	public synchronized void start(IDFSListener aListener) throws DFSException {

		if (aListener == null) {
			throw new NullPointerException();
		}

		_listener = aListener;

		// Even if I am embedded I will add a connection listener to this
		// to know the market data status.
		_server.addListener(aListener);
		// the embedded server is, of course, connected.
		aListener.onConnectionStatusUpdate(ETypeOfData.DFS_PROXY,
				EConnectionStatus.CONNECTED);
	}

	@Override
	public synchronized void stop() {
		_server.removeListener(_listener);
		_server.stop();
	}

	// /**
	// * subscribes to the given symbol.
	// *
	// * @param symbol
	// * the symbol which you want to subscribe to.
	// * @throws DFSException
	// */
	// @Override
	// public synchronized void subscribeQuote(String symbol) throws
	// DFSException {
	// assert (false);
	// // _server.subscribeQuote(_listener, symbol);
	// }

	@Override
	public void truncateMaturity(String aPassword, String aSymbol,
			BarType aType, long truncateLength) throws DFSException {
		_server.truncateMaturity(aPassword, aSymbol, aType, truncateLength);

	}

	@Override
	public synchronized void unsubscribeQuote(String symbol)
			throws DFSException {
		// _server.unsubscribeQuote(_listener, symbol);
	}

	@Override
	public void unWatchDbSymbol(IDatabaseChangeListener aListener,
			String aSymbol) throws DFSException {
		_server.removeWatcher(aListener, aSymbol);
	}

	@Override
	public void watchDbSymbol(IDatabaseChangeListener aListener, String symbol)
			throws DFSException {

		_server.addWatcher(aListener, symbol);
	}
}
