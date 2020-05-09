package com.mfg.plstats.ui;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.persist.PLStatsCSVConfiguration;
import com.mfg.plstats.persist.PLStatsCSVStorage;
import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.ui.views.CommonNavigatorContentProvider;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.PLStatsProbabilitiesStorage;
import com.mfg.widget.probabilities.ProbabilityElement;

public class PLStatsContentProvider extends CommonNavigatorContentProvider {

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof PLStatsCSVStorage) {
			List<PLStatsCSVConfiguration> list = ((PLStatsCSVStorage) parent)
					.getObjects();
			return list.toArray();
		}
		if (parent instanceof PLStatsCSVConfiguration) {
			UUID id = ((PLStatsCSVConfiguration) parent).getUUID();
			List<PLStatsIndicatorConfiguration> list = PLStatsPlugin
					.getDefault().getIndicatorStorage()
					.findIndicatorsBySymbolId(id);
			return list.toArray();
		}

		if (parent instanceof PLStatsIndicatorConfiguration) {
			PLStatsProbabilitiesStorage storate = WidgetPlugin.getDefault()
					.getProbsStorage();
			IIndicatorConfiguration indicator = ((PLStatsIndicatorConfiguration) parent)
					.getIndicator();
			List<ProbabilityElement> list = storate.getDistributions(indicator);
			if (list != null)
				return list.toArray();
		}
		return null;
	}

	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		PLStatsPlugin plugin = PLStatsPlugin.getDefault();
		storages.addAll(Arrays.asList(plugin.getCSVStorage(), plugin
				.getIndicatorStorage(), WidgetPlugin.getDefault()
				.getProbsStorage()));
	}

}
