package com.mfg.dfs.misc;

import com.mfg.common.DFSException;
import com.mfg.dfs.data.HistoryRequest;
import com.mfg.dfs.data.IHistoryFeedListener.EEosStatus;
import com.mfg.utils.IMarketConnectionStatusListener.EConnectionStatus;
import com.mfg.utils.IMarketConnectionStatusListener.ETypeOfData;

/**
 * An offline data feed is a data feed which is able to answer to all the
 * methods in the {@linkplain IDataFeed} interface without doing anything.
 * 
 * <p>
 * This behavior is more coherent with the overall application. In this case all
 * the other objects do not really know (or care) about the online availability
 * of a data feed.
 * 
 * <p>
 * The history tables will in any case try to update themselves. If they do not
 * succeed than they simply remain in a state where they wait for the history to
 * come.
 * 
 * <p>
 * There is probably something to correct because iqFeed is synchronous so
 * proabably we have to change the handling of the history request, otherwise
 * the tables will be stucked there, waiting for a history that will never come.
 * 
 * 
 * @author Sergio
 * 
 */
public class OfflineDataFeed implements IDataFeed {

	public OfflineDataFeed(MultiServer multiServer) {
		multiServer.onConnectionStatusUpdate(ETypeOfData.HISTORICAL,
				EConnectionStatus.DISCONNECTED);
		multiServer.onConnectionStatusUpdate(ETypeOfData.REAL_TIME,
				EConnectionStatus.DISCONNECTED);
	}

	@Override
	public void subscribeToSymbol(String symbol) {
		// throw new DFSException("this is offline!");
	}

	@Override
	public void unsubscribeSymbol(String symbol) {
		// empty

	}

	@Override
	public void start(String connectionString) {
		// empty

	}

	@Override
	public void stop() {
		// empty

	}

	@Override
	public void requestHistory(HistoryRequest aRequest) throws DFSException {
		// throw new DFSException("this is offline!");
		aRequest.getListener().onEndOfStream(EEosStatus.ALL_OK);
	}

	@Override
	public boolean isConnected() {
		return false;
	}

}
