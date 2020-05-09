package com.mfg.symbols.trading.ui.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.ui.commands.AbstractNewStorageObjectHandler;

public class NewTradingConfigurationHandler extends
		AbstractNewStorageObjectHandler<TradingConfiguration> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.ui.commands.AbstractNewStorageObjectHandler#getStorage()
	 */
	@Override
	protected SimpleStorage<TradingConfiguration> getStorage() {
		return SymbolsPlugin.getDefault().getTradingStorage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewStorageObjectHandler#getInitialObjectName
	 * ()
	 */
	@Override
	protected String getInitialObjectName() {
		return "Trading";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewStorageObjectHandler#createNewName(com
	 * .mfg.persist.interfaces.SimpleStorage)
	 */
	@Override
	protected String createNewName(SimpleStorage<TradingConfiguration> storage) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewStorageObjectHandler#createObject(org.
	 * eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	protected TradingConfiguration createObject(ExecutionEvent event) {
		TradingConfiguration configuration = super.createObject(event);

		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		Assert.isTrue(!sel.isEmpty());

		InputConfiguration input = (InputConfiguration) sel.getFirstElement();
		configuration.getInfo().setInputConfiguratioId(input.getUUID());

		SymbolsPlugin symbolsPlugin = SymbolsPlugin.getDefault();

		List<TradingConfiguration> tradings = symbolsPlugin.getTradingStorage()
				.findByInput(input);
		int set;
		if (tradings.isEmpty()) {
			set = symbolsPlugin.getSetsManager().getNextAvailableSet();
		} else {
			TradingConfiguration last = tradings.get(tradings.size() - 1);
			set = last.getInfo().getConfigurationSet();
		}
		configuration.getInfo().setConfigurationSet(set);

		String name = getStorage().createNewName("Trading", tradings);
		configuration.setName(name);

		// hack to use the manual strategy by default
		configuration.getInfo().setStrategyFactoryId(
				"com.mfg.strategy.manual.strategyFactory");

		// AbstractStrategyFactory[] strategyFactories = symbolsPlugin
		// .getStrategyFactories();
		// configuration.getInfo().setStrategyFactoryId(
		// strategyFactories[0].getId());

		return configuration;
	}

}
