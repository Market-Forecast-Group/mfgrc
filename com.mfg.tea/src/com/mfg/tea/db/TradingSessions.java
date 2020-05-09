package com.mfg.tea.db;

import com.mfg.tea.db.Db.OBJECTS;

/**
 * The trading session is the class that stores the information about a trading
 * session.
 * 
 * <p>
 * The fields of the objects are immutable but the object itself is not. That
 * is... we may append data to the object and this append will create new data,
 * the object will "change" in the sense that the trading session will have more
 * orders, etc... but each individual field is immutable.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class TradingSessions {

	private static final String TRADING_RUNS_FIELD = "trRuns";
	private static final String START_FIELD = "start";
	private static final String STOP_FIELD = "stop";
	@SuppressWarnings("unused")
	private static Db _instance;

	/**
	 * Creates a new trading session.
	 * 
	 * @param subTeaId
	 * 
	 * @return the newly created session.
	 */
	public static int createTradingSession(int subTeaId) {
		int newTradingSessionId = DbKeyFieldHelper
				.getNewObjectId(OBJECTS.TRADING_SESSION_OBJECT);

		String now = Db.currentTimeAsDbString();

		DbKeyFieldHelper.putFieldValue(OBJECTS.TRADING_SESSION_OBJECT,
				newTradingSessionId, START_FIELD, now);

		return newTradingSessionId;
	}

	public static void initialize(Db aInstance) {
		_instance = aInstance;
	}

	public static void newTradingRun(int sessionId, int tradingPipeId) {
		DbKeyFieldHelper.putArrayValue(OBJECTS.TRADING_SESSION_OBJECT,
				sessionId, TRADING_RUNS_FIELD, Integer.toString(tradingPipeId));
	}

	static void closeTradingSession(int aTradingSessionId) {

		String now = Db.currentTimeAsDbString();

		DbKeyFieldHelper.putFieldValue(OBJECTS.TRADING_SESSION_OBJECT,
				aTradingSessionId, STOP_FIELD, now);

		/*
		 * Now I delete the trading session sequence key.
		 */
		DbKeyFieldHelper.deleteArrayIndex(Db.OBJECTS.TRADING_SESSION_OBJECT,
				aTradingSessionId, TRADING_RUNS_FIELD);

	}
}
