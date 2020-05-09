package com.mfg.utils;

import com.thoughtworks.xstream.XStream;

/**
 * The base class for an object which can be serialize to a string using the
 * {@link XStream} library.
 */
public abstract class XmlIdentifier extends BasicIdentifier {

	/**
	 * Xstream is static because all the classes use it to serialize themselves
	 */
	private final static transient XStream _xstream = new XStream();

	protected XmlIdentifier() {
		//
	}

	@Override
	public String serializeToString() {
		synchronized (_xstream) {
			return _xstream.toXML(this);
		}

	}

	public static XmlIdentifier createFromString(String serializeToString) {
		synchronized (_xstream) {
			XmlIdentifier basicId = (XmlIdentifier) _xstream
					.fromXML(serializeToString);
			return basicId;
		}

	}

}
