package com.mfg.plstats.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.thoughtworks.xstream.XStream;

public class PLStatsIndicatorStorage extends
		SimpleStorage<PLStatsIndicatorConfiguration> {

	public PLStatsIndicatorStorage() {
		PLStatsPlugin.getDefault().getCSVStorage()
				.addStorageListener(new WorkspaceStorageAdapter() {
					@Override
					public void objectRemoved(IWorkspaceStorage storage,
							Object obj) {
						IStorageObject obj2 = (IStorageObject) obj;
						List<PLStatsIndicatorConfiguration> list = findIndicatorsBySymbolId(obj2
								.getUUID());
						for (PLStatsIndicatorConfiguration obj3 : list) {
							try {
								remove(obj3);
							} catch (RemoveException e) {
								throw new RuntimeException(e);
							}
						}
					}
				});
	}

	@Override
	protected void initDeserializedObject(PLStatsIndicatorConfiguration obj) {
		super.initDeserializedObject(obj);

		// TODO: temporal, we have to delete CSVSymbolData.
		UUID symbolId = obj.getSymbolId();
		PLStatsCSVConfiguration csv = PLStatsPlugin.getDefault()
				.getCSVStorage().findById(symbolId);
		CSVSymbolData symbol = new CSVSymbolData(csv.getFile());
		IIndicatorConfiguration indicator = obj.getIndicator();
		indicator.setSymbol(symbol);
	}

	@Override
	public void configureXStream(XStream x) {
		super.configureXStream(x);
		x.alias("plstats-indicator-config", PLStatsIndicatorConfiguration.class);
		x.alias("indicator-params", IndicatorParamBean.class);
		x.alias("probabilities-settings",
				com.mfg.interfaces.trading.Configuration.class);
	}

	@Override
	public String getStorageName() {
		return "PLStats-Indicator-Configurations";
	}

	@Override
	public PLStatsIndicatorConfiguration createDefaultObject() {
		return new PLStatsIndicatorConfiguration();
	}

	public List<PLStatsIndicatorConfiguration> findIndicatorsBySymbolId(UUID id) {
		List<PLStatsIndicatorConfiguration> list = new ArrayList<>();
		for (PLStatsIndicatorConfiguration obj : getObjects()) {
			if (obj.getSymbolId().equals(id)) {
				list.add(obj);
			}
		}
		return list;
	}

	@Override
	public String getFileName(PLStatsIndicatorConfiguration obj) {
		return obj.getSymbolId() + "-" + obj.getName();
	}

}
