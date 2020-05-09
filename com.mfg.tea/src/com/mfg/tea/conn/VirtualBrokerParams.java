package com.mfg.tea.conn;

import com.mfg.dfs.conn.IDFSListener;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.XmlIdentifier;

/**
 * This is a class that lists all the broker parameters in one place.
 * 
 * <p>
 * This class is serializable to xml because I may of course create a virtual
 * broker outside my process space.
 * 
 * * @param aTradingSymbol
 * 
 * @param subTeaId
 * @param virtualSymbol
 *            every virtual broker is connected to a virtual symbol, which
 *            determines the prices stream, and to a "real" symbol, which is the
 *            real trading symbol which is sent to the broker, but not both.
 * @param tradingSymbol
 *            every virtual broker is attached to a real symbol, for example
 *            ESU14, or anything more complicated, for example a JSON
 *            representation of a Contract (in IB) or something other
 *            broker-dependendent string.
 * 
 *            The application uses this string only for accounting purposes, in
 *            the sense that it will reunion all the virtual brokers which are
 *            attached to the same trading symbol (this will give the
 *            possibility to have a tree of statistics).
 * @param listener
 *            This is the normal callback, it may be a local callback or a proxy
 *            callback, as usual. As in the case of a {@link IDFSListener}.
 * @param isRealTimeRequest
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class VirtualBrokerParams extends XmlIdentifier {

	public VirtualBrokerParams(TickDataRequest aRequest) {
		request = aRequest;
	}

	/**
	 * Every virtual broker is connected to a request, which also defines a
	 * virtual symbol. From the tick data request it is possible, at least
	 * theoretically, to repeat the same trading session.
	 * 
	 * <p>
	 * Theoretically for real time trades, because of course the real time ticks
	 * are not repeatable.
	 */
	public final TickDataRequest request;

	/**
	 * This is the identifier of the portfolio, usually this is a UUID.
	 */
	public String shellId;

	/**
	 * The identifier for the tea, the client which is connected to the
	 * multiTEA, it usually is some sort of "dns" magic, or simply
	 * "localhost:PORT", just to identify the client.
	 */
	public String teaId;

	/**
	 * The virtual symbol id used by the trading pipe to get the data. Every
	 * trading pipe has a virtual symbol.
	 */
	public String virtualSymbol;

	/**
	 * The traded symbol. This is the real traded symbol.
	 */
	public String tradingSymbol;

	/**
	 * if the request is real time then the statistics can be shared for a real
	 * time symbol...
	 */
	public boolean isRealTimeRequest;

	/**
	 * True if the client wants to connect to a paper trading account, the
	 * client is free to have paper trading for some of the requests.
	 */
	public boolean isPaperTradingRequested;

	/**
	 * The callback interface used to signal events in the market. Events may be
	 * called from a different thread.
	 */
	public IVirtualBrokerListener listener;

	/**
	 * Tick size of the symbol
	 */
	public int tickSize;

	/**
	 * 
	 */
	public int tickValue;

}
