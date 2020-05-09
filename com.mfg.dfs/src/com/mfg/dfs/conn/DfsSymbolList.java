package com.mfg.dfs.conn;

import java.util.ArrayList;

import com.mfg.common.DfsSymbol;
import com.thoughtworks.xstream.XStream;

/**
 * This simple class holds the list of prefixes in the system.
 * 
 * <p>
 * When we request the list of symbols to the system we get this class. This is
 * proxy friendly in the sense that we are able to request the list of symbols
 * also to a remote server and we get the answer.
 * 
 * 
 * @author Sergio
 * 
 */
public class DfsSymbolList {
	public ArrayList<DfsSymbol> symbols;
	/**
	 * The list of CSV symbols, the symbols are wihtout the "#csv" suffix.
	 * 
	 * @deprecated I do not see this field is read in any part of the system (arian) 
	 */
	@Deprecated
	public ArrayList<DfsSymbol> csvSymbols;

	@Override
	public String toString() {
		XStream xstream = new XStream();
		String xml = xstream.toXML(this);
		return xml;
	}
}
