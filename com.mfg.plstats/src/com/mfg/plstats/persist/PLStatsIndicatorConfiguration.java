package com.mfg.plstats.persist;

import java.util.UUID;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.AbstractStorageObject;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.plstats.PLStatsPlugin;

public class PLStatsIndicatorConfiguration extends AbstractStorageObject {

	private UUID _symbolId;

	private IIndicatorConfiguration _indicator;

	public PLStatsIndicatorConfiguration() {
	}

	@Override
	public SimpleStorage<?> getStorage() {
		return PLStatsPlugin.getDefault().getIndicatorStorage();
	}

	public UUID getSymbolId() {
		return _symbolId;
	}

	public void setSymbolId(UUID symbolId) {
		_symbolId = symbolId;
	}

	public IIndicatorConfiguration getIndicator() {
		return _indicator;
	}

	public void setIndicator(IIndicatorConfiguration indicator) {
		_indicator = indicator;
	}

}
