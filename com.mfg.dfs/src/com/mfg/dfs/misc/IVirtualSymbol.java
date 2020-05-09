package com.mfg.dfs.misc;

import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.dm.TickDataRequest;

/**
 * A virtual symbol interface which is the base interface for normal dfs symbols
 * and csv symbols.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IVirtualSymbol {

	void fastForward();

	void fullSpeedUntil(int limitTime);

	/**
	 * gets the DFS symbol associated with this virtual symbol.
	 * 
	 * <p>
	 * It is mostly used to get the tick and the scale from the virtual symbol
	 * itself.
	 * 
	 * @return
	 * @throws DFSException
	 */
	DfsSymbol getDfsSymbol() throws DFSException;

	String getId();

	TickDataRequest getRequest();

	void pause();

	void play();

	void setDelay(long delay);

	void start();

	void step();

	void stop();

}
