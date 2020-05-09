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
package com.mfg.symbols.inputs.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.CenterLineAlgo;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.thoughtworks.xstream.XStream;

/**
 * @author arian
 * 
 */
public class InputsStorage extends SimpleStorage<InputConfiguration> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#getStorageName()
	 */
	@Override
	public String getStorageName() {
		return "Inputs-Configurations";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#getFileName(com.mfg.persist.
	 * interfaces.IStorageObject)
	 */
	@Override
	public String getFileName(InputConfiguration obj) {
		IStorageObject symbol = PersistInterfacesPlugin.getDefault().findById(
				obj.getInfo().getSymbolId());
		return symbol.getName() + "-" + super.getFileName(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seee
	 * com.mfg.persist.interfaces.SimpleStorage#configureXStream(com.thoughtworks
	 * .xstream.XStream)
	 */
	@Override
	public void configureXStream(XStream xstream) {
		super.configureXStream(xstream);
		xstream.alias("input-config", InputConfiguration.class);
		xstream.alias("input-config-info", InputConfigurationInfo.class);
		xstream.alias("indicator-params", IndicatorParamBean.class);
		xstream.omitField(InputConfigurationInfo.class, "historicalData");
		xstream.omitField(IndicatorParamBean.class,
				"topBottomMaxDistance_bruteForce");
		xstream.omitField(IndicatorParamBean.class, "negativeOnSCTouch");

		SymbolsPlugin.getDefault();
		List<ISymbolStorageReference> references = SymbolsPlugin
				.getSymbolStorageReferences();
		for (ISymbolStorageReference s : references) {
			s.configureXStream(xstream);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.AbstractWorkspaceStorage#storageLoaded()
	 */
	@SuppressWarnings("deprecation")
	// POLYNOMIAL_FIT needed
	@Override
	public void storageLoaded() {
		super.storageLoaded();
		for (InputConfiguration input : getObjects()) {

			if (input.getInfo().getIndicatorParams()
					.getIndicator_centerLineAlgo() == CenterLineAlgo.POLYNOMIAL_FIT) {
				input.getInfo()
						.getIndicatorParams()
						.setIndicator_centerLineAlgo(CenterLineAlgo.POLYLINES_2);
			}

			if (input.getInfo().getHistoricalDataInfo() == null) {
				IStorageObject symbol = PersistInterfacesPlugin.getDefault()
						.findById(input.getInfo().getSymbolId());
				if (symbol != null) {
					SimpleStorage<?> dfsStorage = symbol.getStorage();
					@SuppressWarnings("unchecked")
					SymbolConfiguration<?, SymbolConfigurationInfo<?>> defSymbol = (SymbolConfiguration<?, SymbolConfigurationInfo<?>>) dfsStorage
							.createDefaultObject();
					HistoricalDataInfo historicalDataInfo = defSymbol.getInfo()
							.getHistoricalDataInfo();
					input.getInfo().setHistoricalDataInfo(historicalDataInfo);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.SimpleStorage#createDefaultObject()
	 */
	@Override
	public InputConfiguration createDefaultObject() {
		return new InputConfiguration();
	}

	public List<InputConfiguration> findBySymbolId(UUID symbolId) {
		List<InputConfiguration> list = new ArrayList<>();
		for (InputConfiguration configuration : getObjects()) {
			if (configuration.getInfo().getSymbolId().equals(symbolId)) {
				list.add(configuration);
			}
		}
		return list;
	}

	/**
	 * @param configuration
	 * @return
	 */
	public InputConfiguration[] findBySymbol(
			SymbolConfiguration<?, ?> configuration) {
		List<InputConfiguration> list = findBySymbolId(configuration.getUUID());
		InputConfiguration[] array = list.toArray(new InputConfiguration[list
				.size()]);
		return array;
	}
}
