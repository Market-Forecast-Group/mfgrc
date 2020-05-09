package com.mfg.tea.conn;

import java.util.ArrayList;

import com.mfg.common.TEAException;
import com.mfg.tea.accounting.LogicalInventoriesHolder;
import com.mfg.tea.accounting.MixedInventoriesFolder;
import com.mfg.tea.db.Db;
import com.mfg.utils.U;

/**
 * An helper class to help the accounting for a single tea.
 * 
 * <p>
 * This class can be considered as the ancestor class for the {@link LocalTEA}
 * and the {@link TEAStub} classes, but I cannot do that because Java does not
 * have multiple inheritance.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
final class SingleTeaHelper {

	// int sessionId;

	LogicalInventoriesHolder _dbSymbolsRoot;

	/**
	 * These are all the brokers attached to this tea, this map is useful when
	 * TEA closes and we have to close all the brokers attached to it.
	 */
	ArrayList<VirtualBrokerBase> _brokers = new ArrayList<>();

	/**
	 * Each single tea has a symbolic container of all the accounts in the
	 * system, it is the logical root, which is linked to the logical root of
	 * the MultiTea server.
	 * 
	 * <p>
	 * The logical root does not have a total equity, as it may have a mixture
	 * of different qualities, for example paper trading and real trading.
	 * 
	 * 
	 * <p>
	 * Maybe this is not useful.
	 * 
	 */
	private LogicalInventoriesHolder _logicalRoot;

	/**
	 * A single tea has its own folder which will contain all the symbols traded
	 * by this sub tea, this is of course a mixed symbol folder because each TEA
	 * could handle different symbols.
	 * 
	 * <p>
	 * This folder holds the equity for all the real time trading, the paper
	 * trading are collected in another part.
	 * 
	 */
	MixedInventoriesFolder _mixedRealTradingSymbols;

	/**
	 * This is the root which gives the <b>total equity</b> for the paper
	 * trading symbols.
	 * 
	 * <p>
	 * Maybe this is the only thing we need to have the possibility to track the
	 * total paper trading equity for this TEA.
	 * 
	 */
	MixedInventoriesFolder _mixedPaperTradingSymbols;

	/**
	 * Every connected TEA has a session identifier which is <b>not</b> its id,
	 * but a monotone increasing integer which is used to identify the trading
	 * session.
	 */
	int _tradingSessionId;

	/**
	 * The tea identifier, this is the human readable string used to distinguish
	 * the subteas from one another.
	 */
	private String _teaId;

	/**
	 * Builds a tea helper with a particular tea identifier.
	 * 
	 * <p>
	 * A tea is also a container of different accounts which are then used to
	 * group the different quantities in TEA.
	 * 
	 * @param teaId
	 * @param aRoot
	 */
	public SingleTeaHelper(String teaId, LogicalInventoriesHolder aRoot) {

		_teaId = teaId;

		/*
		 * This creates a new trading session. I inform the database that the
		 * client teaId is connected. This event will create a tea session
		 * identifier into which trading runs will be registered.
		 * 
		 * If this is the first time the teaId is seen than a new record to
		 * register it is created.
		 * 
		 * The sub tea id is a container for trading sessions, each session can
		 * have one or more runs. Each run can have several orders.
		 */

		_tradingSessionId = Db.i().newTradingSession(teaId);

		_logicalRoot = new LogicalInventoriesHolder(teaId, aRoot);

		_mixedRealTradingSymbols = new MixedInventoriesFolder(teaId,
				(MixedInventoriesFolder) aRoot
						.getChild(LogicalInventoriesHolder.REAL_TIME_FOLDER));

		_mixedPaperTradingSymbols = new MixedInventoriesFolder(
				teaId,
				(MixedInventoriesFolder) aRoot
						.getChild(LogicalInventoriesHolder.PAPER_TRADING_FOLDER));

		_dbSymbolsRoot = new LogicalInventoriesHolder(
				LogicalInventoriesHolder.DATABASE_TRADING_FOLDER, _logicalRoot);

		_logicalRoot.addChild(LogicalInventoriesHolder.DATABASE_TRADING_FOLDER,
				_dbSymbolsRoot);

		_logicalRoot.addChild(LogicalInventoriesHolder.PAPER_TRADING_FOLDER,
				_mixedPaperTradingSymbols);

		_logicalRoot.addChild(LogicalInventoriesHolder.REAL_TIME_FOLDER,
				_mixedRealTradingSymbols);
	}

	/**
	 * removes the string teaId from the active sessions (this session will be
	 * saved in database).
	 * 
	 * @param aTeaId
	 *            the tea id to be unregistered
	 * 
	 * @param aRoot
	 *            the root of all the active sessions.
	 * @throws TEAException
	 */
	@SuppressWarnings("boxing")
	public void unregister(LogicalInventoriesHolder aRoot) throws TEAException {
		((MixedInventoriesFolder) aRoot
				.getChild(LogicalInventoriesHolder.REAL_TIME_FOLDER))
				.removeChild(_teaId);

		((MixedInventoriesFolder) aRoot
				.getChild(LogicalInventoriesHolder.PAPER_TRADING_FOLDER))
				.removeChild(_teaId);

		ArrayList<VirtualBrokerBase> brokers = _brokers;
		if (brokers != null) {

			U.debug_var(737372, "Stopping tea ", _teaId, " there are ",
					brokers.size(), " brokers to forcely stop");

			for (VirtualBrokerBase broker : brokers) {
				broker.forcedStop();
			}
		}

		Db.tradingSessionClosed(_teaId, _tradingSessionId);

	}

	/**
	 * Creates a new trading run in this session.
	 * 
	 * @param vb
	 * @param params
	 */
	public void newTradingRun(VirtualBrokerBase vb, VirtualBrokerParams params) {
		/*
		 * Here I add the broker to the array of brokers held by this particular
		 * tea interface. This broker will be added to the brokers used to
		 * compute the overall performance of this particular tea.
		 */
		this._brokers.add(vb);

		vb.setTradingRunId(Db.newTradingRun(_tradingSessionId, params));
	}

	/**
	 * The trading run is stopping, so I have to remove the virtual broker from
	 * the session.
	 * 
	 * @param aBroker
	 */
	public void closedTradingRun(VirtualBrokerBase aBroker) {
		Db.closedTradingRun(aBroker.getTradingRunId());
		_brokers.remove(aBroker);
	}

	/**
	 * It stores the information about the symbols which are all the same,
	 * inside this tea, we can have the paper trading stocks and the real
	 * trading stocks.
	 * 
	 * <p>
	 * Key is the traded symbol, not the Virtual symbol, which is unique, but
	 * the unique representation of the traded symbol as seen as the REAL
	 * broker.
	 */
	// HashMap<String, HomogeneusInventoriesFolder>
	// _homogeneousRealTradingStocks = new HashMap<>();

	/**
	 * The root for all the paper trading brokers inside this tea.
	 * <p>
	 * The meaning of this map is the same of the
	 * {@link #_homogeneousRealTradingStocks} map, but this is related to paper
	 * trading.
	 */
	// HashMap<String, HomogeneusInventoriesFolder> _paperTradingRoot = new
	// HashMap<>();
}