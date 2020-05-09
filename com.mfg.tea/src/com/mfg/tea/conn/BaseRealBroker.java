package com.mfg.tea.conn;

import com.mfg.tea.accounting.MixedInventoriesFolder;

/**
 * The base class for all the real brokers in the system.
 * 
 * <p>
 * The real broker can be simulated..., but this is not the same as the paper
 * trading, because in this way all the accounts are shared.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class BaseRealBroker implements IRealBroker {

	/**
	 * This is the unique listener of a real broker, it is naturally
	 * {@link MultiTEA}, so I could use the singleton to access it, but it is
	 * simpler and clearer to have the listener here.
	 */
	protected final IRealBrokerListener _listener;

	protected BaseRealBroker(IRealBrokerListener aListener,
			MixedInventoriesFolder multiTEARoot) {
		_listener = aListener;
		_realStocksInventoryRoot = multiTEARoot;
	}

	/**
	 * the base real broker has also the parent of all the inventories.
	 * <p>
	 * * A real broker has of course a manager for all the symbols which are
	 * created in this realm. These symbols are <b>real</b> symbols and are
	 * updated in real time using the real time data (the data provider).
	 * 
	 * <p>
	 * The broker itself may also be simulated but this is transparent by the
	 * outside world.
	 * 
	 * <p>
	 * This tracks all the "things" owned by the client, which is
	 * {@link MultiTEA}.
	 * 
	 * <p>
	 * The concept of "things" is tricky, because it depends on the broker's
	 * type.. a real broker will need real things, like "GOOG" or "BD" or other
	 * real symbols.
	 * 
	 * <p>
	 * It tracks them using the {@link TEAOrder#getTradingSymbol()} method. So
	 * at 1st level for the tree of this account we have all the various
	 * symbols.
	 * 
	 * <p>
	 * This represents the <b>unique</b> real equity that the broker sees,
	 * because this unifies all the real tradings which are done in the overall
	 * system (composed of all the clients which connect to it).
	 * 
	 */
	@SuppressWarnings("unused")
	private MixedInventoriesFolder _realStocksInventoryRoot;

}
