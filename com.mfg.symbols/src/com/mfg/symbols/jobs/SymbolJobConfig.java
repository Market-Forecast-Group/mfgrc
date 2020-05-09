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
package com.mfg.symbols.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.persistence.TradingStorage;

/**
 * @author arian
 * 
 */
public class SymbolJobConfig<T extends SymbolConfiguration<?, ?>> {
	private final T symbol;
	private final InputConfiguration[] inputs;
	private final TradingConfiguration[] tradings;
	private final Map<UUID, List<TradingConfiguration>> map;
	private final InputConfiguration[] inputsToRun;
	private final TradingConfiguration[] tradingsToRun;
	private BaseConfiguration<?> startConfiguration;
	private boolean runWarmupFullSpeed = false;

	public SymbolJobConfig(T aSymbol, List<InputConfiguration> aInputs,
			List<TradingConfiguration> aTradings,
			BaseConfiguration<?> historicalDataContainer) {
		this(aSymbol, aInputs.toArray(new InputConfiguration[aInputs.size()]),
				aTradings.toArray(new TradingConfiguration[aTradings.size()]),
				historicalDataContainer);
	}

	public SymbolJobConfig(T aSymbol, InputConfiguration[] aInputs,
			TradingConfiguration[] aTradings,
			BaseConfiguration<?> aStartConfiguration) {
		super();
		this.symbol = aSymbol;
		this.inputs = aInputs;
		this.tradings = aTradings;
		this.startConfiguration = aStartConfiguration;

		map = new HashMap<>();

		SymbolsPlugin plugin = SymbolsPlugin.getDefault();
		inputsToRun = aInputs == null ? plugin.getInputsStorage().findBySymbol(
				aSymbol) : aInputs;

		if (aTradings == null) {
			TradingStorage storage = plugin.getTradingStorage();
			List<TradingConfiguration> list = storage.findByInput(inputsToRun);
			tradingsToRun = list.toArray(new TradingConfiguration[list.size()]);
		} else {
			tradingsToRun = aTradings;
		}

		for (TradingConfiguration trading : tradingsToRun) {
			UUID inputId = trading.getInfo().getInputConfiguratioId();
			if (!map.containsKey(inputId)) {
				map.put(inputId, new ArrayList<TradingConfiguration>());
			}
			map.get(inputId).add(trading);
		}
	}

	public boolean isRunWarmupFullSpeed() {
		return this.runWarmupFullSpeed;
	}

	public void setRunWarmupFullSpeed(boolean fullspeed) {
		this.runWarmupFullSpeed = fullspeed;
	}

	/**
	 * @return the startConfiguration
	 */
	public BaseConfiguration<?> getStartConfiguration() {
		return startConfiguration;
	}

	public T getSymbol() {
		return symbol;
	}

	public InputConfiguration[] getInputs() {
		return inputs;
	}

	public TradingConfiguration[] getTradings() {
		return tradings;
	}

	public InputConfiguration[] getInputsToRun() {
		return inputsToRun;
	}

	public List<TradingConfiguration> getTradingsToRun(InputConfiguration input) {
		List<TradingConfiguration> list = map.get(input.getUUID());
		return list == null ? Collections.<TradingConfiguration> emptyList()
				: list;
	}

	@SuppressWarnings("rawtypes")
	public static List<SymbolJobConfig<?>> createFromConfigurations(
			final Collection<Object> startConfigurations,
			final Object startConfiguration) {
		Set<Object> configSet = new HashSet<>(startConfigurations);
		List<Object> toAdd = new LinkedList<>();

		// add related inputs
		for (Object config : configSet) {
			if (config instanceof TradingConfiguration) {
				InputConfiguration input = SymbolsPlugin
						.getDefault()
						.getInputsStorage()
						.findById(
								((TradingConfiguration) config).getInfo()
										.getInputConfiguratioId());
				toAdd.add(input);
			}
		}

		configSet.addAll(toAdd);
		toAdd.clear();

		// add related symbols
		for (Object config : configSet) {
			if (config instanceof InputConfiguration) {
				Object symbol = PersistInterfacesPlugin.getDefault().findById(
						((InputConfiguration) config).getInfo().getSymbolId());
				toAdd.add(symbol);
			}
		}
		configSet.addAll(toAdd);

		List<SymbolJobConfig<?>> list = new ArrayList<>();
		for (Object config : configSet) {

			if (config instanceof SymbolConfiguration) {
				// if the config is a symbol, create a job argument.
				SymbolConfiguration symbol = (SymbolConfiguration) config;
				List<InputConfiguration> inputs = new ArrayList<>();
				List<TradingConfiguration> tradings = new ArrayList<>();

				// look for all the related inputs
				for (Object config2 : configSet) {
					if (config2 instanceof InputConfiguration) {
						InputConfiguration input = (InputConfiguration) config2;
						if (input.getInfo().getSymbolId()
								.equals(symbol.getUUID())) {
							inputs.add(input);
							// look for all the related tradings
							for (Object config3 : configSet) {
								if (config3 instanceof TradingConfiguration) {
									TradingConfiguration trading = (TradingConfiguration) config3;
									if (trading == startConfiguration
											|| startConfigurations
													.contains(trading)) {
										if (trading.getInfo()
												.getInputConfiguratioId()
												.equals(input.getUUID())) {
											tradings.add(trading);
										}
									}
								}
							}
						}
					}
				}
				Object startConfiguration2;
				if (startConfiguration == null) {
					if (!startConfigurations.contains(symbol)
							&& inputs.size() == 1) {
						// if started from an input or a trading then we use the
						// input as start configuration
						startConfiguration2 = inputs.get(0);
					} else {
						// else we use the symbol as start configuration
						startConfiguration2 = symbol;
					}
				} else {
					startConfiguration2 = startConfiguration;
				}
				list.add(new SymbolJobConfig<SymbolConfiguration<?, ?>>(symbol,
						inputs, tradings,
						(BaseConfiguration<?>) startConfiguration2));
			}
		}
		return list;
	}
}
