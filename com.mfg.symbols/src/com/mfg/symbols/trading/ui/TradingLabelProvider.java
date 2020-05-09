package com.mfg.symbols.trading.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.ui.ConfigurationSetsManager;
import com.mfg.ui.views.StorageObjectsLabelProvider;

public class TradingLabelProvider extends StorageObjectsLabelProvider {
	private final List<Image> images;

	/**
	 * 
	 */
	public TradingLabelProvider() {
		images = new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		for (Image img : images) {
			img.dispose();
		}
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof TradingConfiguration) {
			SymbolsPlugin
					.getDefault()
					.getSetsManager();
			Image img = ConfigurationSetsManager
					.getImage(
							((TradingConfiguration) element).getInfo()
									.getConfigurationSet());
			return img;
		}
		return super.getImage(element);
	}
}
