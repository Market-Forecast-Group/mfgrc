package com.mfg.tea.conn;

import java.util.List;

/**
 * This interface is the interface used to query the statistics for a live or
 * recorded trading session.
 * 
 * <p>
 * The two modes of the trading sessions are equivalent, the only difference is
 * that a "live" trading session has not a end date, and its equity is only a
 * tentative one.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ITradingSession {
	//

	public List<ITradingPipe> getTradingPipes();
}
