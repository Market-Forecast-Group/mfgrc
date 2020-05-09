package com.mfg.plstats.ui;

import org.eclipse.swt.graphics.Image;

import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.persist.PLStatsCSVConfiguration;
import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.ui.views.StorageObjectsLabelProvider;
import com.mfg.utils.ImageUtils;
import com.mfg.widget.probabilities.ProbabilityElement;

public class PLStatsLabelProvider extends StorageObjectsLabelProvider {
	@Override
	public Image getImage(Object element) {
		if (element instanceof PLStatsCSVConfiguration) {
			return ImageUtils.getBundledImage(PLStatsPlugin.getDefault(),
					"icons/symbol csv.ico");
		}
		if (element instanceof PLStatsIndicatorConfiguration) {
			return ImageUtils.getBundledImage(PLStatsPlugin.getDefault(),
					"icons/indicator.ico");
		}
		if (element instanceof ProbabilityElement) {
			return ImageUtils.getBundledImage(PLStatsPlugin.getDefault(),
					"icons/probability forecasting 16.ico");
		}

		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ProbabilityElement) {
			return ((ProbabilityElement) element).getName();
		}
		return super.getText(element);
	}
}