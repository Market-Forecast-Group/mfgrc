package com.mfg.tea.conn;

/**
 * This is the base class for the two TEAs in system... the local and the proxy
 * one.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class BaseTEA implements ITEA {

	/**
	 * Every TEA (either local or remote) has a client id, which is used to
	 * distinguish the orders of different tea listeners.
	 * 
	 * <p>
	 * This is different from the distinction of orders in a portfolio, because
	 * they are instead differentiated in the trading configuration itself.
	 * 
	 * <p>
	 * So, really, we have different structures...
	 * 
	 * <p>
	 * First the client id, which is the mfg client id, then the trading
	 * configuration id (this is done here, in the local tea), and then the
	 * strategy id (which is done inside the portfolio).
	 */
	protected final String _id;

	protected BaseTEA(String aId) {
		_id = aId;
	}

}
