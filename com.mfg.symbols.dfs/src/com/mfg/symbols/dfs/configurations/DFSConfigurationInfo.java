package com.mfg.symbols.dfs.configurations;

import java.util.HashMap;

import com.mfg.common.BarType;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;

public class DFSConfigurationInfo extends
		SymbolConfigurationInfo<DFSSymbolData> {
	private String prefix;
	private HashMap<BarType, DfsIntervalStats> intervals;

	public DFSConfigurationInfo() {
		setSymbol(new DFSSymbolData());
		setHistoricalDataInfo(new DFSHistoricalDataInfo());
	}

	public void setPrefix(String aPrefix) {
		this.prefix = aPrefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setIntervalMap(HashMap<BarType, DfsIntervalStats> aIntervals) {
		this.intervals = aIntervals;
	}

	public HashMap<BarType, DfsIntervalStats> getIntervals() {
		return intervals;
	}
}
