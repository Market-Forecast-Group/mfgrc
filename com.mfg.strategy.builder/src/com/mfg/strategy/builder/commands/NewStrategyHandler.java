
package com.mfg.strategy.builder.commands;

import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.ui.commands.AbstractNewStorageObjectHandler;

public class NewStrategyHandler extends AbstractNewStorageObjectHandler<StrategyInfo> {

	@Override
	protected SimpleStorage<StrategyInfo> getStorage() {
		return StrategyBuilderPlugin.getDefault().getStrategiesStorage();
	}


	@Override
	protected String getInitialObjectName() {
		return "Pattern";
	}
}
