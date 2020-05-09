package com.mfg.symbols.dfs.ui;

import java.util.Map.Entry;

import org.eclipse.swt.graphics.Image;

import com.marketforecastgroup.dfsa.ui.DFSSymbolsLabelProvider;
import com.mfg.common.BarType;
import com.mfg.dfs.data.DfsIntervalStats;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.ui.widgets.SymbolsLabelProvider;
import com.mfg.utils.ImageUtils;

public class DFSLabelProvider extends SymbolsLabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof DFSConfiguration) {
			return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
					SymbolsPlugin.SYMBOL_CONFIG_IMAGE_PATH);
		}

		if (element instanceof Entry) {
			Entry<?, ?> entry = (Entry<?, ?>) element;
			if (entry.getValue() instanceof DfsIntervalStats) {
				EVisibleState state = ((DfsIntervalStats) entry.getValue()).state;
				return DFSSymbolsLabelProvider.getMaturityStateImage(state);
			}
		}

		if (element == DFSContentProvider.WAITING_FOR_DFS) {
			return null;
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element == DFSContentProvider.WAITING_FOR_DFS) {
			return "Waiting for DFS...";
		}

		if (element == DFSContentProvider.CONTENT_ROOT) {
			return "DFS";
		}

		if (element instanceof Entry) {
			Entry<?, ?> entry = (Entry<?, ?>) element;
			if (entry.getKey() instanceof BarType) {
				return entry.getKey().toString();
			}
		}

		return super.getText(element);
	}
}
