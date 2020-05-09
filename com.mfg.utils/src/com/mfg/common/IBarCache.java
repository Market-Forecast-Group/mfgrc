package com.mfg.common;

/**
 * The interface used to get the bars from the database, used only by the gui,
 * this interface is (of course) not available remotely, because it is
 * bidirectional.
 * 
 * <p>
 * The cache is a read only version of the table, with random access
 * 
 * <p>
 * The cache is like the view, it can be regarded as an "active" view in the
 * sense that has the methods to access the read only portion of the table and
 * to be notified when events happens in the cache itself, for example a new
 * complete bar arrives.
 * 
 * <p>
 * The cache can be a direct view of the mdb table in dfs or it can be a proxy
 * version of this view, because the real table is elsewhere. But this is not
 * important, as long as the client access the bar cache only through the
 * interface.
 * 
 * <p>
 * The cache is an {@linkplain Iterable} of type bar; this is used in the warm
 * up phase.
 * 
 * @author Sergio
 * 
 */
public interface IBarCache extends AutoCloseable, Iterable<Bar> {

	/**
	 * closes the cache and frees the resources.
	 * 
	 * <p>
	 * This close is for most caches a no operation, because in this way we have
	 * the possibility to inform the table that the cache is not used any more.
	 * 
	 * <p>
	 * After the close this cache is invalid. Do not call any other method,
	 * because it will throw a {@linkplain NullPointerException}
	 * 
	 * @throws DFSException
	 * 
	 */
	@Override
	public void close() throws DFSException;

	/**
	 * returns the bar at a certain index.
	 * 
	 * @param index
	 *            must be between 0 inclusive and size() exclusive
	 * @return a newly created bar
	 * @throws DFSException
	 */
	public Bar getBar(int index) throws DFSException;

	/**
	 * returns the maturity corresponding to this cache.
	 * 
	 * @return
	 */
	public Maturity getMaturity();

	/**
	 * returns the symbol corresponding to this cache
	 * 
	 */
	public DfsSymbol getSymbol();

	/**
	 * this is different from {@link #close()}, as the close method will render
	 * this cache useless.
	 * 
	 * <p>
	 * This method instead frees the resources for this cache stored so far but
	 * mantains the cache active, so it is safe to continue to use it, but if
	 * the client tries to access the past bars an exception is thrown
	 * 
	 * <p>
	 * After this method the {@link #size()} of the cache is not changed, but
	 * the real size, that is the size of the bars stored is one, that is only
	 * one bar is retained (to compute the bar duration of the next bar).
	 * 
	 * <p>
	 * Clients which use the cache for real time are encouraged to call this
	 * method from time to time to avoid infinite expansion (albeit there is
	 * enough room for thousands of bars).
	 * 
	 * @param lastAffirmedsize
	 *            the size which has been last affirmed. The cache after the
	 *            purge will ensure that the bar at index (lastAffirmedSize-1)
	 *            is still reachable.
	 * 
	 */
	public void purgeOldBars(int lastAffirmedsize);

	/**
	 * returns the size of this bar cache.
	 * 
	 * @return
	 */
	public int size();

	// public void checkNewBar();
}
