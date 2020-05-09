
package com.mfg.strategy.builder.views;

import org.eclipse.ui.navigator.CommonNavigator;

public class StrategyNavigator extends CommonNavigator {

	public static final String ID = "com.mfg.strategy.builder.views.StrategyNavigator"; //$NON-NLS-1$


	@Override
	protected Object getInitialInput() {
		return StrategyContentProvider.ROOT;
	}
}
