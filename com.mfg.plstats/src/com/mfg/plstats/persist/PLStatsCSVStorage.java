package com.mfg.plstats.persist;

import com.mfg.persist.interfaces.SimpleStorage;
import com.thoughtworks.xstream.XStream;

public class PLStatsCSVStorage extends SimpleStorage<PLStatsCSVConfiguration> {

	@Override
	public void configureXStream(XStream x) {
		super.configureXStream(x);
		x.alias("plstats-csv-config", PLStatsCSVConfiguration.class);
	}

	@Override
	public String getStorageName() {
		return "PLStats-CSV-Configurations";
	}

	@Override
	public PLStatsCSVConfiguration createDefaultObject() {
		return new PLStatsCSVConfiguration();
	}

}
