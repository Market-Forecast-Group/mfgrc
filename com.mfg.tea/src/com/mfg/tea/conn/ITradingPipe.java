package com.mfg.tea.conn;

import java.util.List;

/**
 * Each trading session can have one or possible more trading pipes. In a
 * certain sense the trading session is act of logging in to TEA.
 * 
 * <p>
 * The trading pipe is actually the act of having a handle by which putting
 * orders.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ITradingPipe {

	/**
	 * lists all the events in this trading pipe run.
	 * 
	 * <p>
	 * The events are
	 * 
	 * @return
	 */
	public List<IEvent> getEvents();
}
