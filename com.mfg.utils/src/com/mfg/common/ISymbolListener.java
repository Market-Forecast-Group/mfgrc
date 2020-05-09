package com.mfg.common;

/**
 * Simple interface to listen to quotes.
 * 
 * @author Sergio
 * 
 */
public interface ISymbolListener {

	/**
	 * called when a new quote for a particular symbol comes.
	 * 
	 * <p>
	 * The quote is a complex data, because now in DFS there can be also virtual
	 * quotes, which are the result of the expansion of the symbol. That means
	 * that the "quote" itself may be a message, for example the end of
	 * expansion or a message that says that the real time queue is full or too
	 * crowded
	 * 
	 * <p>
	 * The complexity of the handling of the various quote types is entirely on
	 * the shoulders of the receiver.
	 * 
	 * 
	 * @param anEvent
	 *            the quote which is given by DFS, this quote includes all the
	 *            attributes, also the fake time, if pertinent.
	 * 
	 *            The event could also be not a quote, but an event which is
	 *            created to signal particular notifications.
	 */

	public void onNewSymbolEvent(DFSSymbolEvent anEvent);
}
