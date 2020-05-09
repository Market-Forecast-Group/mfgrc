package com.mfg.dfs.conn;

/**
 * interface used to know some events inside the DFS.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDFSObserver {

	/**
	 * This method is called when the given symbol has finished its first
	 * initialization phase. (and it should be listed in the ready symbols).
	 * 
	 * @param symbol
	 *            the symbol which has been initialized
	 */
	public void onSymbolInitializationEnded(String symbol);

	/**
	 * Called when the scheduler starts running; it will in general try to
	 * correct the data which is already present, and, if there are symbols to
	 * be collected, it will collect new data. During this period the DFS is in
	 * a state which does not allow any other scheduling active. The real time
	 * subscriptions, however, continue to arrive to it.
	 */
	public void onSchedulerStartRunning();

	/**
	 * Called when the observers stops a cycle. The manual scheduling is now
	 * again possible.
	 */
	public void onSchedulerEndedCycle();

}
