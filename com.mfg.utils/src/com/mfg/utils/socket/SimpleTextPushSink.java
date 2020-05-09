package com.mfg.utils.socket;

import java.io.IOException;

/**
 * A push sink is an object which is able to collect push messages.
 * 
 * <p>
 * It can be regarded as a persistent request. Infact a request does only live
 * for the time being, when it waits for the answer. Instead a
 * {@linkplain PushSink} is active until the proxy removes it.
 * 
 * <p>
 * As the name implies the push sink is a collector for the push messages from
 * the server. A push message is a message which has not a corresponding request
 * message.
 * 
 * <p>
 * A push sink can also be regarded as a multistring answer, because we can have
 * a multiple string (for example multiple bars), but this is not
 * 
 * @author Sergio
 * 
 */
public abstract class SimpleTextPushSink {
	/**
	 * This is the id of the push sink, all the pushes which start with this id
	 * will be directed to this sink.
	 */
	public final String _pushId;

	/**
	 * A simple boolean used to know if the sink is over and if it should be
	 * removed from the system.
	 * 
	 */
	protected boolean _sinkOver = false;

	/**
	 * Creates the push sink; the push sink is then used as a container for all
	 * the messages which come from the server (the stub).
	 * <p>
	 * Each push sink has an id, that id is used to distinguish the various
	 * sinks in the proxy.
	 * 
	 * @param aPushId
	 *            this is the identifier of the push which is used to
	 *            distinguish the various push sinks.
	 */
	public SimpleTextPushSink(String aPushId) {
		_pushId = aPushId;
	}

	/**
	 * 
	 * @param payload
	 *            the push which has come from the socket. It is in the form of
	 *            <code>p,$sinkId,$payload</code>
	 *            <p>
	 *            We get here the payload part, so without the first 2 fields.
	 * @throws IOException
	 *             if something is wrong.
	 */
	public abstract void handlePush(String payload) throws IOException;

	/**
	 * returns true if the sink has finished to get the push (maybe for time
	 * requests or something like that).
	 * 
	 * @return
	 */
	public boolean isSinkOver() {
		return _sinkOver;
	}
}
