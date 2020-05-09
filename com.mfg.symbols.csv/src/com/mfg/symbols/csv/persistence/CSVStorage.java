package com.mfg.symbols.csv.persistence;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.mfg.connector.csv.CSVHistoricalDataInfo;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.configurations.CSVConfigurationInfo;
import com.mfg.symbols.csv.configurations.CSVSymbolData2;
import com.mfg.utils.Utils;
import com.thoughtworks.xstream.XStream;

/**
 * @author arian
 * 
 */
public class CSVStorage extends SimpleStorage<CSVConfiguration> {
	public CSVStorage() {
		Utils.debug_id(734677, "create storage");
	}

	@Override
	public CSVConfiguration createDefaultObject() {
		return new CSVConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.persistence.SimpleStorage#getStorageName()
	 */
	@Override
	public String getStorageName() {
		return "CSV-Symbol-Configurations";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.persist.interfaces.SimpleStorage#initDeserializedObject(java.
	 * io.File, com.mfg.persist.interfaces.IStorageObject)
	 */
	@Override
	protected void initDeserializedObject(CSVConfiguration obj) {
		super.initDeserializedObject(obj);
		CSVSymbolData2 symbol = obj.getInfo().getSymbol();
		if (symbol.getRealTickSize() == null) {
			Assert.isTrue(symbol.getTickSize() == null);
			Assert.isTrue(symbol.getTickScale() == null);
		} else {
			Assert.isTrue(symbol.getTickSize() != null);
			Assert.isTrue(symbol.getTickSize().intValue() == symbol
					.getRealTickSize().unscaledValue().intValue());

			Assert.isTrue(symbol.getTickScale() != null);
			Assert.isTrue(symbol.getTickScale().intValue() == symbol
					.getRealTickSize().scale());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.persistence.SimpleStorage#configureXStream(com.
	 * thoughtworks.xstream.XStream)
	 */
	@Override
	public void configureXStream(XStream xstream) {
		super.configureXStream(xstream);
		configureXStream2(xstream);
	}

	public static void configureXStream2(XStream xstream) {
		xstream.alias("csv-config", CSVConfiguration.class);
		xstream.alias("csv-config-info", CSVConfigurationInfo.class);
		xstream.alias("csv-symbol", CSVSymbolData2.class);
		xstream.alias("csv-historical-data-info", CSVHistoricalDataInfo.class);
		// TODO: Mar 10, 2013
		xstream.omitField(CSVHistoricalDataInfo.class, "gap");
		// TODO: Feb 23, 2013
		xstream.omitField(SymbolConfigurationInfo.class, "historicalData");
		xstream.omitField(CSVConfigurationInfo.class,
				"historicalDataNumberOfPrices");
		// TODO: Feb 24, 2013
		xstream.omitField(CSVConfigurationInfo.class, "historicalData");
	}

	@Override
	public void loadAll(File workspace) {
		super.loadAll(workspace);
		migrateToRelativePath();
	}

	@SuppressWarnings("deprecation")
	private void migrateToRelativePath() {
		List<CSVConfiguration> list = getObjects();
		for (CSVConfiguration conf : list) {
			CSVSymbolData2 symbol = conf.getInfo().getSymbol();
			File file = symbol.getFile();
			if (file != null) {
				String name = file.toPath().getFileName().toString();
				Utils.debug_id(456632, "Migrate CSV symbol: " + file + " ---> "
						+ name);
				symbol.setFileName(name);
				symbol.setFile(null);
			}
		}
	}
}