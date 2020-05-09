package com.mfg.dfs.data;

import java.util.ArrayList;
import java.util.List;

import com.mfg.common.BarType;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.utils.U;
import com.thoughtworks.xstream.XStream;

/**
 * This is the status of a symbol composed of different maturities and that has
 * a continuous contract.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DfsMaturitySymbolStatus extends DfsSymbolStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4489190545137380930L;

	public DfsMaturitySymbolStatus(DfsSymbol aSymbol) {
		super(aSymbol);
	}

	/**
	 * simply factory method to return a fake symbol status.
	 * 
	 * <p>
	 * This is used to test the Arian's interface and also the tcp/ip mechanism
	 * which is used with the payload etc...
	 * 
	 * @return a fake DfsSymbolStatus with fake data...
	 */
	public static DfsSymbolStatus createFakeSymbolStatus() {
		DfsMaturitySymbolStatus dfs = new DfsMaturitySymbolStatus(
				new DfsSymbol("ES", "Complete name", 25, 2, 25));

		dfs.maturityStats = new ArrayList<>();

		MaturityStats ms = new MaturityStats(new Maturity());

		DfsIntervalStats val = new DfsIntervalStats(EVisibleState.COMPLETE,
				999, 100, 1000);
		// val.startDate = 100;
		// val.endDate = 1000;
		// val.numBars = 999;
		ms._map.put(BarType.MINUTE, val);

		val = new DfsIntervalStats(EVisibleState.COMPLETE, 9993, 1, 100900);
		// val.startDate = 1;
		// val.endDate = 100990;
		// val.numBars = 9993;
		ms._map.put(BarType.DAILY, val);

		val = new DfsIntervalStats(EVisibleState.COMPLETE, 329, 100, 554);
		// val.startDate = 100;
		// val.endDate = 554;
		// val.numBars = 329;
		ms._map.put(BarType.RANGE, val);

		dfs.maturityStats.add(ms);

		return dfs;
	}

	public static void main(String args[]) {
		DfsSymbolStatus dfs = createFakeSymbolStatus();

		XStream xstream = new XStream();
		xstream.addDefaultImplementation(ArrayList.class, List.class);
		xstream.alias("Database", List.class);
		String xml = xstream.toXML(dfs);
		System.out.println(xml);

		DfsSymbolStatus dfs1 = (DfsSymbolStatus) xstream.fromXML(xml);
		U.dump(dfs1);

	}

}
