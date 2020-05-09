package com.mfg.dfs.data;

import com.mfg.common.BarType;

/**
 * this is a class which is used to query all the available historical data.
 * 
 * <p>Different data feeds will have different syntaxes to do so, in any case
 * for the point of view of DFS we simply want all the data from oldest to newest for
 * a given symbol and for a given bar type.
 * 
 * <p>How this request is converted in a real request to the data feed is of course
 * data feed dependent.
 * 
 * @author Sergio
 *
 */
public class AllAvailableHistoricalData extends HistoryRequest {

	

	public AllAvailableHistoricalData(IHistoryFeedListener aListener, String symbol, BarType aType){
		super(aListener, symbol, aType);
	}
	
	
}
