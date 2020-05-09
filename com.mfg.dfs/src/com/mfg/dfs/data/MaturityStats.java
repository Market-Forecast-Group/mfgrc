package com.mfg.dfs.data;

import java.util.HashMap;

import com.mfg.common.BarType;
import com.mfg.common.Maturity;
import com.thoughtworks.xstream.XStream;

/**
 * Simple class which holds the data for a maturity.
 * 
 * <p>
 * It gives to the client the possibility to know whether there is some data in
 * the given maturity and how much.
 * 
 * @author Sergio
 * 
 */
public class MaturityStats {

	private Maturity _maturity;

	/**
	 * This is the map which for every bar type lists the statistics of the
	 * interval. This is used to know the intervals (and to display them in the
	 * editor).
	 */
	public HashMap<BarType, DfsIntervalStats> _map = new HashMap<>();

	public MaturityStats(Maturity aMaturity) {
		_maturity = aMaturity;
	}

	public Maturity getMaturity() {
		return _maturity;
	}

	@Override
	public String toString() {
		XStream xstream = new XStream();
		String xml = xstream.toXML(this);
		return xml;
	}

}
