package com.mfg.dfs.data;

import com.mfg.common.BarType;


/**
 * A <b>partial</b> history request.
 * 
 * <p>Partial means that the data is not fully asked, but instead the application asks
 * for a partial historical feed.
 * 
 * <p>Dfs is able also to have an interval request, for example from a start to an end date, but
 * in our case this is not so important.
 * 
 *  
 * @author Sergio
 *
 */
public class PartialHistoryRequest extends HistoryRequest {

	/**
	 * This number could be number of days, of minutes or number of seconds (in case of range bars).
	 */
	private final long _fromDate;

	/**
	 * creates a partial history request.
	 * <p>A partial history is characterized by a time interval which is the number
	 * of units to look before.
	 * 
	 * <p>The unit of this interval depends 
	 * 
	 * @param aListener
	 * @param aSymbol
	 * @param beginDate this is the begin date of this history request.
	 */
	public PartialHistoryRequest(IHistoryFeedListener aListener,
			String aSymbol, BarType aType, long beginDate) {
		super(aListener, aSymbol, aType);
		
		_fromDate = beginDate;
	}

	public long getBeginDate() {
		return _fromDate;
	}

}
