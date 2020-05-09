
package com.mfg.strategy.builder;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.mfg.strategy.AbstractStrategyFactory;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.IStrategySettings;
import com.mfg.strategy.automatic.EventsPatternStrategy;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;
import com.mfg.strategy.builder.ui.AutomaticStrategySettingsComposite;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.IDashboardWidgetProvider;
import com.thoughtworks.xstream.XStream;

public class AutomaticStrategyFactory extends AbstractStrategyFactory {
	public static final String ID = "com.mfg.strategy.builder.strategyFactory";


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#createDefaultSettings()
	 */
	@Override
	public IStrategySettings createDefaultSettings() {
		AutomaticStrategySettings settings = new AutomaticStrategySettings();
		List<StrategyInfo> list = StrategyBuilderPlugin.getDefault().getStrategiesStorage().getObjects();
		if (!list.isEmpty()) {
			settings.setStrategyInfoId(list.get(0).getUUID());
		}
		return settings;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#configureXStream(com.thoughtworks.xstream.XStream)
	 */
	@Override
	public void configureXStream(XStream xstream) {
		xstream.alias("aumatic-strategy-settings", AutomaticStrategySettings.class);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#createSettingsEditor(com.mfg.strategy.IStrategyFactory.CreateSettingsEditorArgs)
	 */
	@Override
	public void createSettingsEditor(CreateSettingsEditorArgs args) {
		AutomaticStrategySettingsComposite comp = new AutomaticStrategySettingsComposite(args.getParent(), SWT.NONE);
		AutomaticStrategySettings settings = (AutomaticStrategySettings) args.getSettings();
		comp.setSettings(settings);
		args.setBindings(comp.getDataBindingContext());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#createTradingPageActions(com.mfg.strategy.IStrategyFactory.CreateTradingPageActionsArgs)
	 */
	@Override
	public Action[] createTradingPageActions(CreateTradingPageActionsArgs args) {
		return null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#createStrategy(com.mfg.strategy.IStrategyFactory.CreateStrategyArgs)
	 */
	@Override
	public FinalStrategy createStrategy(CreateStrategyArgs args) {
		AutomaticStrategySettings settings = (AutomaticStrategySettings) args.getSettings();
		StrategyBuilderStorage storage = StrategyBuilderPlugin.getDefault().getStrategiesStorage();
		StrategyInfo info = storage.findById(settings.getStrategyInfoId());
		EventsPatternStrategy strategy = StrategyBuilderPlugin.createStrategyFromInfo(info, args.getLogger());
		return strategy;
	}


	@Override
	public IDashboardWidgetProvider createDashboardWidget(TradingConfiguration conf) {
		return new AutomaticStrategyDashboardProvider(conf);
	}
}
