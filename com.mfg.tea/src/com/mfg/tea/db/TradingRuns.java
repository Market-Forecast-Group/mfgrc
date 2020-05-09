package com.mfg.tea.db;

import com.mfg.tea.conn.VirtualBrokerParams;
import com.mfg.tea.db.Db.OBJECTS;

/**
 * This package contains methods and constants to help manage the trading pipes
 * in the database.
 * 
 * <p>
 * For the database the trading pipe is equivalent to a "run". A trading session
 * can have multiple runs, with or without the same parameters.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 *
 */
public class TradingRuns {

	private static final String START_FIELD = "start";
	private static final String STOP_FIELD = "stop";
	private static final String ORDERS_LIST = "orders";
	@SuppressWarnings("unused")
	private static Db _instance;

	/**
	 * 
	 * creates the a trading pipe which is used to identify a virtual broker
	 * inside the trading session.
	 * 
	 * <p>
	 * This will then be the "father" for all the orders...
	 * 
	 * @param sessionId
	 * @param params
	 * @return
	 */
	static int createTradingRun(int sessionId, VirtualBrokerParams params) {
		int tradingRunId = DbKeyFieldHelper
				.getNewObjectId(OBJECTS.TRADING_RUN_OBJECT);
		String now = Db.currentTimeAsDbString();
		DbKeyFieldHelper.putFieldValue(OBJECTS.TRADING_RUN_OBJECT,
				tradingRunId, START_FIELD, now);

		/*
		 * Serialize the virtual broker parameters...
		 */

		return tradingRunId;
	}

	static void closeTradingRun(int aTradingRunId) {
		String now = Db.currentTimeAsDbString();
		DbKeyFieldHelper.putFieldValue(OBJECTS.TRADING_RUN_OBJECT,
				aTradingRunId, STOP_FIELD, now);

		DbKeyFieldHelper.deleteArrayIndex(OBJECTS.TRADING_RUN_OBJECT,
				aTradingRunId, ORDERS_LIST);
	}

	static void initialize(Db aInstance) {
		_instance = aInstance;
	}

	/**
	 * adds the order identified by parameter dbId to the list of this trading
	 * run.
	 * 
	 * @param aRunIdentifier
	 * @param dbId
	 */
	public static void newOrderAdded(int aRunIdentifier, long dbId) {
		DbKeyFieldHelper.putArrayValue(OBJECTS.TRADING_RUN_OBJECT,
				aRunIdentifier, ORDERS_LIST, Long.toString(dbId));
	}

}
