package com.mfg.utils;


/**
 * interface used to listen to changes in the connection status of the market.
 * 
 * <p>
 * This means that the
 * 
 * @author Sergio
 * 
 */
public interface IMarketConnectionStatusListener {
	/**
	 * The data feed has two types of data: historical and real time.
	 * 
	 * <P>
	 * And then we have the dfs proxy itself, which is present only if the
	 * connection is remote. If the dfs is embedded this part is not present and
	 * the connection to dfs proxy is always on, because there is not a dfs
	 * proxy.
	 * 
	 * 
	 * <p>
	 * Each of these two data feeds can be connected or not.
	 * 
	 * @author Sergio
	 * 
	 */
	public enum ETypeOfData {
		HISTORICAL, REAL_TIME, DFS_PROXY
	}

	public enum EConnectionStatus {
		CONNECTING, CONNECTED, DISCONNECTED
	}

	public void onConnectionStatusUpdate(ETypeOfData aDataType,
			EConnectionStatus aStatus);
}
