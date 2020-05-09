
package com.mfg.strategy.builder.persistence;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.strategy.builder.StrategyBuilderPlugin;

public class StrategyBuilderStorageReference implements IWorkspaceStorageReference {

	@Override
	public StrategyBuilderStorage getStorage() {
		return StrategyBuilderPlugin.getDefault().getStrategiesStorage();
	}


	@Override
	public String getStorageId() {
		return StrategyBuilderStorage.class.getCanonicalName();
	}

}
