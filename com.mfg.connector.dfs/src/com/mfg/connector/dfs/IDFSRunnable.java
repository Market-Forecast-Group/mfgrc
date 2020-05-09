package com.mfg.connector.dfs;

import com.mfg.common.DFSException;
import com.mfg.dfs.conn.IDFS;

/**
 * Interface to request DFS. Sometimes DFS delays a lot to get ready so users
 * can listen to the notReady() method to show a busy message, specially in GUI
 * components.
 * 
 * @author Arian
 * 
 */
public interface IDFSRunnable {
	/**
	 * This method is called if DFS is not ready yet. Implementations can show a
	 * busy message.
	 */
	public void notReady();

	/**
	 * This method is called when DFS gets ready.
	 * 
	 * @param dfs
	 */
	public void run(IDFS dfs) throws DFSException;
}
