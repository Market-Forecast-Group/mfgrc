package com.mfg.dfs.data;

import java.io.Serializable;
import java.util.List;

import com.mfg.dfs.conn.SocketServer;
import com.mfg.utils.socket.ISimpleClient;
import com.mfg.utils.socket.SimpleTextClientsModel;
import com.thoughtworks.xstream.XStream;

/**
 * a model (in the <i>Model / View / Controller</i> paradigm) of all the clients
 * connected to the server.
 * <p>
 * This model is used by the GUI to make a view of all the clients connected to
 * it.
 * <p>
 * The view can be a listener for this model.
 * <p>
 * This class is serializable only because I need it for xstream and it is only
 * used to debug the model status using the console. No other uses for this.
 * 
 * @author Sergio
 * 
 */
public class DfsClientsModel extends SimpleTextClientsModel implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1060327545324741492L;
	private SocketServer _instance;

	public DfsClientsModel(SocketServer _instance1) {
		this._instance = _instance1;
	}

	/**
	 * used mainly to serialize this class on the socket (if needed), but for
	 * the most part this serialization is only used for testing purposes.
	 */
	@Override
	public String toString() {
		XStream xstream = new XStream();
		String xml = xstream.toXML(_instance);
		return xml;
	}

	/**
	 * gets an iterator over all the collection of clients...
	 * 
	 * @return the iterator
	 */
	public List<ISimpleClient> getClients() {
		return _instance.getClients();
	}

}
