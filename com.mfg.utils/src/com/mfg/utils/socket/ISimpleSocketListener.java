package com.mfg.utils.socket;

/**
 * interface for a {@linkplain SimpleSocketTextClient}.
 * 
 * <p>
 * It has only two methods. One normal method which is used to process a line at
 * a time, the other which is used to communicate abnormal conditions in the
 * socket itself.
 * 
 * @author Sergio
 * 
 */
public interface ISimpleSocketListener {

	/**
	 * Processes a single line of text.
	 * 
	 * <p>
	 * The socket server should check the return code of the process line,
	 * because this means that the server and client are out of sync for some
	 * reason and, maybe, it is better to switch all the game off.
	 * 
	 * @param line
	 * @return true if the line has been correctly processed, false otherwise
	 */
	public boolean processLine(String line);

	/**
	 * called when the underlying socket is dead. The client will try to
	 * reconnect automatically, if this behavior is not wanted then the user can
	 * simply disconnect explicitly the client using the method
	 * {@linkplain SimpleSocketTextClient#stop()}
	 */
	public void onLostConnection();

	/**
	 * called when the connection is re-estabilished.
	 * 
	 */
	public void onConnectionEstabilished();

	/**
	 * Called when the background thread tries to reconnect. This is followed by
	 * a {@link #onConnectionEstabilished()} message or
	 * {@link #onLostConnection()} message
	 */
	public void onTryingToConnect();

}
