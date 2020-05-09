package com.mfg.symbols.jobs;

import com.mfg.common.TEAException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dm.TickDataRequest;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.tea.conn.ITEA;
import com.mfg.tea.conn.ITEAListener;
import com.mfg.tea.conn.IVirtualBroker;
import com.mfg.tea.conn.IVirtualBrokerListener;
import com.mfg.tea.conn.TEAFactory;
import com.mfg.tea.conn.VirtualBrokerParams;

/**
 * The class that provides the tea to all the trading configurations.
 * 
 * <p>
 * This is where the distinction between local and remote tea is done.
 * 
 * <p>
 * We have only one tea in the system, either local or remote, this because if
 * the tea is remote we have only one socket towards tea.
 * 
 * <p>
 * The {@link TEAGateway} can support multiple listeners, which are simply
 * different {@link TradingPipe} object.
 * 
 * <p>
 * I have to think about the GUI, this because the GUI needs maybe a LOT of
 * listeners attached to the TEA.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TEAGateway implements ITEAListener {

	/*
	 * The tea gateway is the entry point for all the sub-TEAs which will be
	 * created in this application. As it is a singleton it manages the creation
	 * of the sub-TEAs and their dispatching to the trading pipes (or other
	 * object) which need them
	 * 
	 * There is NOT a sub tea any more, or better the fact is that we have a
	 * virtual broker which is tied to a particular virtual symbol.
	 * 
	 * Inside the virtual broker we may have different patterns which use the
	 * same symbol.
	 */

	/**
	 * The singleton gateway which can provide to the rest of the application
	 * the tea interface.
	 */
	private static TEAGateway _instance = null;

	public static TEAGateway instance(IDFS _dataProvider) throws TEAException {
		if (_instance == null) {
			_instance = new TEAGateway(_dataProvider);
		}

		return _instance;
	}

	/**
	 * The only {@link ITEA} object which is inside the application.
	 */
	private ITEA _Tea;

	/**
	 * Cannot directly create the gateway.
	 * 
	 * @param _dataProvider
	 * @throws TEAException
	 */
	private TEAGateway(IDFS _dataProvider) throws TEAException {

		/*
		 * Here from configuration we will know if tea is created local or
		 * remote, with a certain id or not, here we also handle the
		 * authentication part (?) if needed of this sub tea.
		 */

		if (SymbolsPlugin.getDefault().getPreferenceStore()
				.getBoolean(SymbolsPlugin.EMBEDDED_TEA)) {
			_Tea = TEAFactory.createLocalTea(this, _dataProvider, "testid",
					false);
		} else {
			_Tea = TEAFactory
					.createRemoteTea("TESTID", this, "localhost", 8998);
		}
	}

	/**
	 * returns the next virtual broker available, aka SubTea.
	 * 
	 * <p>
	 * The virtual broker uses a virtual symbol to identify the trades.
	 * 
	 * <p>
	 * The virtual symbol may or may not be a real symbol in any case, depending
	 * on tea type, it will connect to a DFS to have the prices.
	 * 
	 * <p>
	 * The symbol has a connection to the UUID of the trading pipe which is
	 * used, because in this way the TEA is able to distinguish the data.
	 * 
	 * @param aVirtualSymbol
	 * @param portfolio
	 * @param isRealTimeRequest
	 *            if the request is real time the TEA will create a virtual
	 *            broker differently and the accounting will be done together
	 *            other trading pipes on the same real symbol. Otherwise if the
	 *            request is database then each symbol runs on its own, it is
	 *            totally separated by the others and the accounting cannot be
	 *            merged (because they are snapshots of trading at different
	 *            times, so there is no such thing as a "symbol" status, because
	 *            they are not comparable).
	 * 
	 * @param paperTrading
	 *            true if the trading configuration wants to connect to a
	 *            simulated broker
	 * @param aShellId
	 *            the shell identifier for this trading pipe. Usually a uuid.
	 * @param aRequest
	 * @param aTickSize
	 * @param aTickValue
	 * 
	 * @return the subtea {@link SingleSimulBroker} which is able to trade the
	 *         symbol given.
	 * 
	 * 
	 * @throws TEAException
	 */
	public synchronized IVirtualBroker createVirtualBroker(
			String aVirtualSymbol, String aTradingSymbol,
			IVirtualBrokerListener portfolio, boolean isRealTimeRequest,
			boolean paperTrading, String aShellId, TickDataRequest aRequest,
			int aTickSize, int aTickValue) throws TEAException {

		VirtualBrokerParams vbp = new VirtualBrokerParams(aRequest);
		vbp.virtualSymbol = aVirtualSymbol;
		vbp.tradingSymbol = aTradingSymbol;
		vbp.listener = portfolio;
		vbp.isRealTimeRequest = isRealTimeRequest;
		vbp.isPaperTradingRequested = paperTrading;
		vbp.shellId = aShellId;
		vbp.tickSize = aTickSize;
		vbp.tickValue = aTickValue;

		return _Tea.createVirtualBroker(vbp);
	}

	/**
	 * This call has done by the different {@link TradingPipe} objects.
	 * 
	 * <p>
	 * Each of them has a different view of the trading pipe, because in this
	 * way the object is used to distinguish the different trading.
	 * 
	 * <p>
	 * In dfs this distinction is not used, because the method returns
	 * immediately.
	 * 
	 * @return the unique tea interface which is in this process.
	 */
	public ITEA getTea() {
		return _Tea;
	}

	/**
	 * In this method the connection status is updated in this singleton.
	 */
	@Override
	public void onConnectionStatusUpdate(ETypeOfConnection aDataType,
			EConnectionStatus aStatus) {
		// TO DO Auto-generated method stub

	}
}
