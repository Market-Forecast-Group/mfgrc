package com.mfg.dfs.data;

import java.io.Serializable;
import java.util.ArrayList;

import com.mfg.common.DfsSymbol;
import com.thoughtworks.xstream.XStream;

/**
 * This class holds the status of the symbol.
 * 
 * <p>
 * The status of the symbol comphrehends all the characteristics of a given
 * symbol which are its nomenclature (name, tick, value etc) which are
 * independent from the data provider and also the characteristics of the symbol
 * inside DFS, that is its amount of data stored, the start, end date for the
 * various time frames (range, daily, minute).
 * 
 * 
 * @author Sergio
 * 
 */
public class DfsSymbolStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6680994311339760285L;

	public final DfsSymbol symbol;

	/**
	 * The maturity stats are ordered from the oldest to the newest. This is a
	 * snapshot of the current data which is present in the server.
	 */
	public ArrayList<MaturityStats> maturityStats = new ArrayList<>();

	/**
	 * The statistics for the continous part.
	 */
	public MaturityStats continuousStats;

	// Constructor valid only for testing.
	public DfsSymbolStatus(DfsSymbol aSymbol) {
		this.symbol = aSymbol;
	}

	@Override
	public String toString() {
		XStream xstream = new XStream();
		String xml = xstream.toXML(this);
		return xml;
	}

}
