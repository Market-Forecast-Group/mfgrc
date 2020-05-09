package com.mfg.tea.conn;


/**
 * This interface is used to collect the data from the ITEA.
 * 
 * 
 * <p>
 * This is the push interface, the notifications that come from the real broker
 * are asynchronous here.
 * 
 * <p>
 * TEA has in any case also a blocking interface, so this is only used when
 * there is an asynchronous notification to give to the outside.
 * 
 * 
 * <p>
 * Every TEA, either local or remote, implements this interface. In the case of
 * the remote listener there will be, as usual, a proxy and a stub.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ITEAListener {

	public enum ETypeOfConnection {
		BROKER, TEA_PROXY
	}

	public enum EConnectionStatus {
		CONNECTING, CONNECTED, DISCONNECTED
	}

	/**
	 * 
	 * @param aDataType
	 * @param aStatus
	 */
	public void onConnectionStatusUpdate(ETypeOfConnection aDataType,
			EConnectionStatus aStatus);

}
