/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.trading.persistence;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import com.mfg.persist.interfaces.RemoveException;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.strategy.AbstractStrategyFactory;
import com.mfg.strategy.IStrategyFactory;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.persistence.InputsStorage;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfigurationInfo;
import com.thoughtworks.xstream.XStream;

/**
 * @author arian
 * 
 */
public class TradingStorage extends SimpleStorage<TradingConfiguration> {
	public static final String PROP_CONFIGURATION_SET = "trading_configurationSet";

	public TradingStorage() {
		addStorageListener(new TradingStorageListener());
	}

	public void fireConfigurationSetChanged(TradingConfiguration configuration) {
		getPropertySupport().firePropertyChange(
				new PropertyChangeEvent(configuration, PROP_CONFIGURATION_SET,
						Boolean.TRUE, Boolean.FALSE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#initDeserializedObject(java.
	 * io.File, com.mfg.persist.interfaces.IStorageObject)
	 */
	@Override
	protected void initDeserializedObject(TradingConfiguration obj) {
		super.initDeserializedObject(obj);
		// checking for old configurations
		if (obj.getInfo().getStrategyFactoryId() == null) {
			AbstractStrategyFactory[] list = SymbolsPlugin.getDefault()
					.getStrategyFactories();
			if (list.length > 0) {
				// hack to put as default the manual strategy
				obj.getInfo().setStrategyFactoryId(
						"com.mfg.strategy.manual.strategyFactory");
				// obj.getInfo().setStrategyFactoryId(list[0].getId());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.AbstractWorkspaceStorage#
	 * addChangeListenerToAddedObject(java.lang.Object)
	 */
	@Override
	protected void addChangeListenerToAddedObject(Object obj) {
		super.addChangeListenerToAddedObject(obj);
		((TradingConfiguration) obj).getInfo().addPropertyChangeListener(
				propertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#getStorageName()
	 */
	@Override
	public String getStorageName() {
		return "Trading-Configurations";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#getFileName(com.mfg.persist.
	 * interfaces.IStorageObject)
	 */
	@Override
	public String getFileName(TradingConfiguration obj) {
		InputsStorage inputsStorage = SymbolsPlugin.getDefault()
				.getInputsStorage();
		InputConfiguration input = inputsStorage.findById(obj.getInfo()
				.getInputConfiguratioId());
		String name = inputsStorage.getFileName(input);
		return name + "-" + obj.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#configureXStream(com.thoughtworks
	 * .xstream.XStream)
	 */
	@Override
	public void configureXStream(XStream xstream) {
		super.configureXStream(xstream);
		xstream.alias("trading-config", TradingConfiguration.class);
		xstream.alias("trading-config-info", TradingConfigurationInfo.class);
		xstream.omitField(TradingConfigurationInfo.class, "brokerFactoryId");
		xstream.omitField(TradingConfigurationInfo.class, "brokerSettingsMap");

		AbstractStrategyFactory[] strategyList = SymbolsPlugin.getDefault()
				.getStrategyFactories();
		for (IStrategyFactory factory : strategyList) {
			factory.configureXStream(xstream);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#createDefaultObject()
	 */
	@Override
	public TradingConfiguration createDefaultObject() {
		return new TradingConfiguration();
	}

	/**
	 * @param parentElement
	 * @return
	 */
	public List<TradingConfiguration> findByInput(InputConfiguration... inputs) {
		List<TradingConfiguration> list = new ArrayList<>();
		for (InputConfiguration input : inputs) {
			for (TradingConfiguration config : getObjects()) {
				if (config.getInfo().getInputConfiguratioId()
						.equals(input.getUUID())) {
					list.add(config);
				}
			}
		}
		return list;
	}

	public List<TradingConfiguration> findBySet(int set) {
		List<TradingConfiguration> list = new ArrayList<>();
		for (TradingConfiguration config : getObjects()) {
			if (config.getInfo().getConfigurationSet() == set) {
				list.add(config);
			}
		}
		return list;
	}

	/**
	 * @param input
	 * @throws RemoveException
	 */
	public void removeByInput(InputConfiguration input) throws RemoveException {
		for (TradingConfiguration trading : new ArrayList<>(getObjects())) {
			if (trading.getInfo().getInputConfiguratioId()
					.equals(input.getUUID())) {
				remove(trading);
			}
		}
	}
}
