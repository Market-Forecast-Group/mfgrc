package com.mfg.tea.conn;

import java.util.List;

/**
 * The interface which is used to list all the methods used to query the TEA.
 * 
 * <p>
 * This interface may be the same for a particular TEA or for the
 * {@link MultiTEA} object, but for this I have to think about it a bit more.
 * 
 * 
 * <P>
 * This interface is the same for the offline (closed) session and the online
 * (current) session.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ITEAQuery {

	/**
	 * Gets the trading sessions between some particular dates for this
	 * particular TEA (or TEA clusters).
	 */
	public List<ITradingSession> getTradingSessionsBetween(long start, long end);

	/**
	 * other methods relative to the status of this TEA, maybe the online
	 * stats???
	 */
}
