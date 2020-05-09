package com.mfg.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * The class stores the various different request parameters which are possible
 * for the DFS server.
 * 
 * <p>
 * This class is only used to create the parameters, it is not meant to contain
 * data but treat it like a normal POD.
 * 
 * 
 * I want to support several types of requests, but mainly those:
 * <P>
 * regarding the interval of time or bars.
 * <P>
 * a. the last n. days of bars
 * <P>
 * b. the last n. bars
 * <P>
 * c. the bars between x and y in time (where x < y)
 * <P>
 * d. x bars after y in time (where of course there are x bars, otherwise all
 * bars are returned)
 * <P>
 * e. x bars before y in time (where of course y must be greater than the first
 * bar's time)
 * <P>
 * f. ALL THE BARS (useful for the GUI browser in process, not very useful for
 * the client).
 * 
 * 
 * regarding the type of bars
 * <P>
 * 1. the daily bars
 * <P>
 * 2. the range bars
 * <P>
 * 3. the x minute bars (where x is a number between 1 and 1440 (one day));
 * <P>
 * 
 * 
 * 
 * @author Sergio
 * 
 */
public class RequestParams {

	/**
	 * This is the request type which is used to differentiate the various
	 * historical requests which are possible for dfs.
	 * 
	 * @author Sergio
	 * 
	 */
	public enum ERequestType {
		NUM_DAYS, NUM_BARS, BETWEEN_TIME_X_Y, X_BARS_AFTER_Y, X_BARS_BEFORE_Y, ALL_BARS
	}

	private static SimpleDateFormat _reqHistoryFormat;

	static {
		_reqHistoryFormat = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
	}

	public static RequestParams createAllBarsRequestDaily(String symbol)
			throws DFSException {
		return new RequestParams(symbol, ERequestType.ALL_BARS, BarType.DAILY,
				1, 0, 0, 0);
	}

	public static RequestParams createAllBarsRequestRange(String symbol)
			throws DFSException {
		return new RequestParams(symbol, ERequestType.ALL_BARS, BarType.RANGE,
				1, 0, 0, 0);
	}

	public static RequestParams createRequestLastDailyDays(String symbol,
			int nDays) throws DFSException {
		return new RequestParams(symbol, ERequestType.NUM_DAYS, BarType.DAILY,
				1, nDays, 0, 0);
	}

	/**
	 * @param barWidth
	 *            for now it is fixed to 1. We don't yet support x minute bars,
	 *            with x != 1.
	 */
	public static RequestParams createRequestLastMinuteDays(String symbol,
			int nDays, int barWidth) throws DFSException {
		return new RequestParams(symbol, ERequestType.NUM_DAYS, BarType.MINUTE,
				barWidth, nDays, 0, 0);
	}

	// second type of request, last number of bars.
	public static RequestParams createRequestLastNumDailyBars(String symbol,
			int nBars) throws DFSException {
		return new RequestParams(symbol, ERequestType.NUM_BARS, BarType.DAILY,
				1, nBars, 0, 0);
	}

	public static RequestParams createRequestLastNumMinuteBars(String symbol,
			int nBars, int barWidth) throws DFSException {
		return new RequestParams(symbol, ERequestType.NUM_BARS, BarType.MINUTE,
				barWidth, nBars, 0, 0);
	}

	public static RequestParams createRequestLastNumRangeBars(String symbol,
			int nBars) throws DFSException {
		return new RequestParams(symbol, ERequestType.NUM_BARS, BarType.RANGE,
				1, nBars, 0, 0);
	}

	public static RequestParams createRequestLastRangeDays(String symbol,
			int nDays) throws DFSException {
		return new RequestParams(symbol, ERequestType.NUM_DAYS, BarType.RANGE,
				1, nDays, 0, 0);
	}

	public static RequestParams createRequestSlot(String symbol,
			long startDate, int numOfBars, BarType barType, int barWidth)
			throws DFSException {
		return new RequestParams(symbol, ERequestType.X_BARS_AFTER_Y, barType,
				barWidth, numOfBars, startDate, -1);
	}

	/**
	 * Parses a string with the comma separated version of this object.
	 * 
	 * <p>
	 * The times are in New York time zone
	 * 
	 * <p>
	 * this is an example of request:
	 * <p>
	 * 
	 * <code>symb,ALL_BARS,DAILY,1,0,19691231 190001,19691231 190002</code>
	 * 
	 * <p>
	 * Not all fields are significant for the request parameters, but if the
	 * user uses the class only with the factory methods then nothing strange
	 * will happen.
	 * 
	 * @param line
	 * @return
	 * @throws DFSException
	 */
	public static RequestParams parse(String line) throws DFSException {
		// first of all I have to split them
		String splits[] = line.split(",");
		if (splits.length != 7) {
			throw new DFSException("cannot parse " + line);
		}

		String symbol = splits[0];
		// String reqId = splits[1];
		ERequestType type = ERequestType.valueOf(splits[1]);
		BarType barType = BarType.valueOf(splits[2]);
		int barWidth = Integer.parseInt(splits[3]);
		int nBarDays = Integer.parseInt(splits[4]);
		long startTime = 0;
		long endTime = 0;
		try {
			startTime = _reqHistoryFormat.parse(splits[5]).getTime();
			endTime = _reqHistoryFormat.parse(splits[6]).getTime();

		} catch (ParseException e) {
			e.printStackTrace();
			throw new DFSException(e);
		}

		return new RequestParams(symbol, type, barType, barWidth, nBarDays,
				startTime, endTime);
	}

	private final String _symbol;

	private final ERequestType _reqType;

	private final BarType _barType;

	private final int _barWidth;

	private final int _nBarsOrDays; // this is the number of bars or days

	private final long _startTime;

	// /First type of request: last days bars.

	private final long _endTime;

	/**
	 * This private constructor is only used from the helper methods. It takes
	 * all the fields for convenience, but not all the fields are useful for all
	 * types of requests.
	 * 
	 * @param symbol
	 * @param type
	 * @param barType
	 * @param barWidth
	 * @param nBarDays
	 * @param startTime
	 * @param endTime
	 * @param reqId
	 * @throws DFSException
	 */
	private RequestParams(String aSymbol, ERequestType aType, BarType aBarType,
			int aBarWidth, int aNorD, long aStartTime, long aEndTime)
			throws DFSException {

		_symbol = aSymbol;
		_reqType = aType;
		_barType = aBarType;
		_nBarsOrDays = aNorD;
		_startTime = aStartTime;
		_endTime = aEndTime;
		_barWidth = aBarWidth;

		if (_reqType == ERequestType.BETWEEN_TIME_X_Y && _startTime > _endTime) {
			throw new DFSException("inconsistent times.");
		}

		if (_reqType != ERequestType.ALL_BARS
				&& _reqType != ERequestType.BETWEEN_TIME_X_Y) {
			if (_nBarsOrDays < 0) {
				throw new DFSException("invalid number of bars or days");
			}
		}

	}

	public BarType getBarType() {
		return _barType;
	}

	public int getBarWidth() {
		return _barWidth;
	}

	public long getEndTime() {
		return _endTime;
	}

	public int getNumBarsOrDays() {
		return _nBarsOrDays;
	}

	/**
	 * @return the request type, it is only meaningful inside the dfs package.
	 */
	public ERequestType getReqType() {
		return _reqType;
	}

	public long getStartTime() {
		return _startTime;
	}

	/**
	 * @return the symbol associated to this request.
	 */
	public String getSymbol() {
		return _symbol;
	}

	/**
	 * an open request is a request that has a right end not closed, that bars
	 * will continue to be created. So it is a dependent quality on the type of
	 * the request, there is not a "real time flag" as there was before.
	 * 
	 * <p>
	 * Actually the real time flag exists and it is inside the TickDataRequest
	 * object.
	 * 
	 * @return true if this is an open ended request.
	 */
	public boolean isOpenRequest() {
		switch (_reqType) {
		case ALL_BARS:
			return false;
		case BETWEEN_TIME_X_Y:
			return false;
		case NUM_BARS:
			return true;
		case NUM_DAYS:
			return true;
		case X_BARS_AFTER_Y:
			return false;
		case X_BARS_BEFORE_Y:
			return false;
		}

		throw new IllegalStateException();
	}

	/**
	 * 
	 * @return a string representation of this object, useful for socket.
	 *         <p>
	 *         The string is serialized with the dates formatted using the Iq
	 *         history date format
	 * 
	 */
	public String serialize() {
		return this._symbol + "," + this._reqType + "," + this._barType + ","
				+ this._barWidth + "," + this._nBarsOrDays + ","
				+ _reqHistoryFormat.format(new Date(this._startTime)) + ","
				+ _reqHistoryFormat.format(new Date(this._endTime));
	}

	/**
	 * Creates a request of last daily day bars.
	 * 
	 * @param symbol
	 * @param startDate
	 * @param numberOfBarsRequested
	 * @return
	 * @throws DFSException
	 */
	public static RequestParams createRequestHistDailyDays(String symbol,
			long startDate, int numberOfBarsRequested) throws DFSException {
		long aEndTime = startDate + numberOfBarsRequested * Yadc.ONE_DAY_MSEC;
		return new RequestParams(symbol, ERequestType.BETWEEN_TIME_X_Y,
				BarType.DAILY, 1, 1, startDate, aEndTime);
	}

	public static RequestParams createRequestHistNumDailyBars(String symbol,
			long startDate, int numberOfBarsRequested) throws DFSException {
		return new RequestParams(symbol, ERequestType.X_BARS_AFTER_Y,
				BarType.DAILY, 1, numberOfBarsRequested, startDate, -1);
	}

	public static RequestParams createRequestNumDaysOfMinuteBarsSince(
			String symbol, int numberOfBarsRequested, int width, long startDate)
			throws DFSException {
		long aEndTime = startDate + numberOfBarsRequested + Yadc.ONE_DAY_MSEC;
		return new RequestParams(symbol, ERequestType.BETWEEN_TIME_X_Y,
				BarType.MINUTE, width, -1, startDate, aEndTime);
	}

	public static RequestParams createRequestNumMinuteBarsSince(String symbol,
			int numberOfBarsRequested, int width, long startDate)
			throws DFSException {
		return new RequestParams(symbol, ERequestType.X_BARS_AFTER_Y,
				BarType.MINUTE, width, numberOfBarsRequested, startDate, -1);
	}

	public static RequestParams createRequestNumDaysOfRangeSince(String symbol,
			long startDate, int numberOfBarsRequested) throws DFSException {
		long aEndTime = startDate + numberOfBarsRequested + Yadc.ONE_DAY_MSEC;
		return new RequestParams(symbol, ERequestType.BETWEEN_TIME_X_Y,
				BarType.RANGE, 1, -1, startDate, aEndTime);

	}

	public static RequestParams createRequestNumBarsOfRangeSince(String symbol,
			long startDate, int numberOfBarsRequested) throws DFSException {
		return new RequestParams(symbol, ERequestType.X_BARS_AFTER_Y,
				BarType.RANGE, 1, numberOfBarsRequested, startDate, -1);
	}

}
