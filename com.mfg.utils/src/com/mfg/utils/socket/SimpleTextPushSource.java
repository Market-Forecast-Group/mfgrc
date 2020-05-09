package com.mfg.utils.socket;

import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Every push source has a key which is the primary key of every subsequent push
 * message.
 * 
 * <p>
 * A push source usually is continuous, in the sense that it continues forever
 * until someone does delete it.
 * 
 * <p>
 * Some other push sources have a finite time limit.
 * 
 * <p>
 * A push source does not do a "multitask" of the source: if for example a
 * source needs to give one million rows it will give one million rows.
 * 
 * <p>
 * the difference is that the push source will have the possibility to be
 * interrupted from the outside. Every push source is required to pool at
 * regular intervals the presence of something which should interrupt it.
 * 
 * @param aPushKey
 */
public abstract class SimpleTextPushSource implements IPushSource {

	@Override
	public void interruptRequest() {
		_endRequested.set(true);
	}

	public final boolean isEnded() {
		return _ended;
	}

	protected boolean _ended = false;

	/**
	 * A stub will have its handle, which will be passed to the clients.
	 */
	private static long _nextHandle = Double.doubleToLongBits(Math.random());

	/**
	 * This boolean is true when we have a request to end.
	 */
	protected AtomicBoolean _endRequested = new AtomicBoolean(false);

	/**
	 * This is the push key which is used to
	 */
	protected final String _pushKey;

	protected final SimpleTextServerStub _stub;

	/**
	 * This is the request done to the push source to push all the information
	 * to the socket. Usually this request is done in another thread, because
	 * the main thread of the stub is waiting for a request from the reader
	 * thread.
	 * 
	 * <p>
	 * So... basically we request here a push to the push source. This push may
	 * (or may not) be long or complete, in any case the push source should pool
	 * regurarly the interrupt request flag in order to know wether it should
	 * quit or not.
	 * 
	 * <p>
	 * This is not enforced (doing so will in any case alter the semantic of the
	 * doPush without in any case doing too much harm).
	 * 
	 * @throws SocketException
	 */
	public abstract void doPush() throws SocketException;

	/**
	 * Sends a string to the socket.
	 * 
	 * <p>
	 * The string is decorated with the {@link #_pushKey} which univocally
	 * defines the push source in the system.
	 * 
	 * @param aPayLoad
	 *            the string to be sent in the socket. The string is not
	 *            formatted, it should have been already formatted.
	 * 
	 * @throws SocketException
	 *             if something is wrong with the push
	 * 
	 */
	protected void sendToSocket(String aPayLoad) throws SocketException {
		synchronized (_stub) {
			String sent = "p," + this._pushKey + "," + aPayLoad;
			_stub.printLine(sent);
		}
	}

	/**
	 * A hook called when the push source is going to be deleted.
	 */
	public void aboutToBeCollected() {
		//
	}

	protected SimpleTextPushSource(SimpleTextServerStub aStub) {
		_stub = aStub;
		_pushKey = Long.toHexString(_nextHandle++);
	}

	@Override
	public final String getPushKey() {
		return _pushKey;
	}
}
