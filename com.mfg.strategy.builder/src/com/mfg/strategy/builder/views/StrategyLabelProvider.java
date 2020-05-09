
package com.mfg.strategy.builder.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.utils.ImageUtils;

public class StrategyLabelProvider extends LabelProvider {

	@Override
	public String getText(Object obj) {
		if (obj instanceof StrategyInfo) {
			return ((StrategyInfo) obj).getName();
		}
		return super.getText(obj);
	}


	@Override
	public Image getImage(Object obj) {
		if (obj instanceof StrategyInfo) {
			return ImageUtils.getBundledImage(StrategyBuilderPlugin.getDefault(),
					"icons/automatic.ico");
		}
		return super.getImage(obj);
	}
}
