package com.mfg.strategy.manual;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import com.mfg.strategy.AbstractStrategyFactory;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.IStrategySettings;
import com.mfg.strategy.ManualStrategySettings;
import com.mfg.strategy.manual.ui.ManualStrategySettingsComposite;
import com.mfg.strategy.manual.ui.commands.OpenTradingConsoleAction;
import com.thoughtworks.xstream.XStream;

public class ManualStrategyFactory extends AbstractStrategyFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#createDefaultSettings()
	 */
	@Override
	public IStrategySettings createDefaultSettings() {
		return new ManualStrategySettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.IStrategyFactory#configureXStream(com.thoughtworks.xstream
	 * .XStream)
	 */
	@Override
	public void configureXStream(XStream xstream) {
		xstream.alias("manual-strategy-settings", ManualStrategySettings.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.IStrategyFactory#createTradingPageActions(com.mfg.strategy
	 * .IStrategyFactory.CreateTradingPageActionsArgs)
	 */
	@Override
	public Action[] createTradingPageActions(CreateTradingPageActionsArgs args) {
		return new Action[] { new OpenTradingConsoleAction(
				args.getConfiguration()) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.IStrategyFactory#createSettingsEditor(com.mfg.strategy
	 * .IStrategyFactory.CreateSettingsEditorArgs)
	 */
	@Override
	public void createSettingsEditor(CreateSettingsEditorArgs args) {
		ManualStrategySettingsComposite comp = new ManualStrategySettingsComposite(
				args.getParent(), SWT.NONE,
				(ManualStrategySettings) args.getSettings());
		args.setBindings(comp.getDataBindingContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.IStrategyFactory#createStrategy(com.mfg.strategy.
	 * IStrategyFactory.CreateStrategyArgs)
	 */
	@Override
	public FinalStrategy createStrategy(CreateStrategyArgs args) {
		ManualStrategy strategy = new ManualStrategy();
		strategy.setManualStrategySettings((ManualStrategySettings) args
				.getSettings());
		return strategy;
	}

}
