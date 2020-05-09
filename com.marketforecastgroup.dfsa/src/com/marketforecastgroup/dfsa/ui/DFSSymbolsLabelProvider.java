package com.marketforecastgroup.dfsa.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.marketforecastgroup.dfsa.DFSAPlugin;
import com.marketforecastgroup.dfsa.ui.SymbolsContentProvider.IntervalInfo;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.utils.ImageUtils;

public class DFSSymbolsLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element == SymbolsContentProvider.WAITING_FOR_DFS) {
			return null;
		}

		if (element instanceof DfsSymbol) {
			return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
					"/icons/symbol.gif");
		}

		if (element instanceof MaturityStats) {
			return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
					"/icons/maturity.gif");
		}

		if (element instanceof SymbolsContentProvider.IntervalInfo) {
			return getMaturityStateImage(((SymbolsContentProvider.IntervalInfo) element).interval.state);
		}

		return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
				"/icons/symbol group.ico");
	}

	public static Image getMaturityStateImage(EVisibleState state) {
		if (state != null) {
			switch (state) {
			case COMPLETE:
				return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
						"/icons/elem_cyan.png");
			case UP_TO_DATE:
				return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
						"/icons/elem_green.png");
			case TRUNCATED:
				return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
						"/icons/elem_red.png");
			}
		}
		return ImageUtils.getBundledImage(DFSAPlugin.getDefault(),
				"/icons/generic_element.gif");
	}

	@Override
	public String getText(Object element) {
		if (element == SymbolsContentProvider.WAITING_FOR_DFS) {
			return "Waiting for DFS...";
		}
		if (element instanceof DfsSymbol) {
			return ((DfsSymbol) element).prefix;
		}

		if (element instanceof MaturityStats) {
			Maturity mat = ((MaturityStats) element).getMaturity();
			return mat == null ? "Continuous Contract" : mat.toFileString();
		}

		if (element instanceof SymbolsContentProvider.IntervalInfo) {
			IntervalInfo info = (SymbolsContentProvider.IntervalInfo) element;
			return info.type.name();
		}

		if (element instanceof String) {
			// this is the case of collecting symbols
			return element + " (collecting)";
		}
		
		return super.getText(element);
	}
}
