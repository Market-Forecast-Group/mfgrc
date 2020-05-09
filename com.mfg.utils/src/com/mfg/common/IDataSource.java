package com.mfg.common;

/**
 * The common interface for a data source from a data provider.
 * 
 * <p>
 * The data will come in form of {@link DFSQuote} objects.
 * 
 * 
 * <p>
 * The object which creates this interface does not need to subscribe to a
 * symbol. It is the "master" object which has this possibility to control the
 * data source (if it is controllable, in its minimum the capability is to
 * start/stop it, like a normal real time data source).
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface IDataSource {

	/**
	 * adds a listener to this data source.
	 * 
	 * <p>
	 * The listener will be able to have the data source data but it won't be
	 * able to control it.
	 * 
	 * <p>
	 * The listener is added to the front of the queue or to the back of the
	 * queue based on the parameter given. This is impoortant because some
	 * listeners need to be informed first of others.
	 * 
	 * @param aListener
	 * 
	 * @param addInFront
	 *            true if you want to add this listener to the front of the data
	 *            source.
	 */
	public void addListener(ISymbolListener aListener, boolean addInFront);

	/**
	 * removes the listener from this data source.
	 * 
	 * <p>
	 * The listener will be removed from the data source. No check is made if
	 * the listener was already unsubscribed, so it is safe to remove it twice.
	 * 
	 * @param aListener
	 */
	public void removeListener(ISymbolListener aListener);

	/**
	 * Starts the data source.
	 * 
	 * <p>
	 * if the data source is controllable it starts itself at a normal speed,
	 * usually one price per second. If the data source is "mixed" (real time)
	 * then the data source will start at the maximum speed and it won't be
	 * controllable, the ticks will come from the market at the "normal"
	 * speed... (the market itself may be controllable if it is a simulated data
	 * feed, but this is not here).
	 * 
	 * @throws DFSException
	 *             if the data source is not controllable.
	 */
	public void start() throws DFSException;

	/**
	 * After the stop method is called, the data source is not more usable, at
	 * least by this client. It is not an error to call it twice (under the hood
	 * there is a unsubscription which is harmless if done twice).
	 * 
	 * @throws DFSException
	 */
	public void stop() throws DFSException;

	/**
	 * 
	 * @throws DFSException
	 */
	public void pause() throws DFSException;

	/**
	 * Sets the delay (in milliseconds) for this data source.
	 * 
	 * @param delay
	 * @throws DFSException
	 */
	public void setDelay(long delay) throws DFSException;

	/**
	 * Makes a database request similar to a mixed request, there will be no
	 * delay at all.
	 * 
	 * <p>
	 * You can pass it a fake limit time when the data source will automatically
	 * stop. If you pass it a really big number, for example
	 * {@link Integer#MAX_VALUE}, the data source will never stop.
	 * 
	 * <p>
	 * It is not an error to call it twice, the limit time will be simply
	 * overwritten.
	 * 
	 * @param limitTime
	 *            the (fake) time when the data source will pause.
	 */
	public void fullSpeedUntil(int limitTime) throws DFSException;

	/**
	 * returns the id of this data source. This unique id is then used by mfg to
	 * tell other participants in the run how to link to this data source, even
	 * remotely (for example TEA).
	 * 
	 * @return the id (unique in server's space, not globally unique) of this
	 *         data source.
	 */
	public String getId();

	/**
	 * plays the data source, if the data source is paused it will restart the
	 * data source.
	 * 
	 * @throws DFSException
	 */
	public void play() throws DFSException;

	/**
	 * Fast forwards the data source.
	 * 
	 * @throws DFSException
	 */
	public void fastForward() throws DFSException;

	/**
	 * Steps the data source, advancing one tick at a time.
	 * 
	 * @throws DFSException
	 */
	public void step() throws DFSException;

}
