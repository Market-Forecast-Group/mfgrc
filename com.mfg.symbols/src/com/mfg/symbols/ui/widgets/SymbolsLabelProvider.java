package com.mfg.symbols.ui.widgets;

import org.eclipse.swt.graphics.Image;

import com.mfg.dm.IDataProvider;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.ui.views.StorageObjectsLabelProvider;
import com.mfg.utils.ImageUtils;

public class SymbolsLabelProvider extends StorageObjectsLabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof SymbolData2) {
			return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
					SymbolsPlugin.SYMBOL_IMAGE_PATH);
		} else if (element instanceof SymbolConfiguration) {
			return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
					SymbolsPlugin.SYMBOL_CONFIG_IMAGE_PATH);
		}

		return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
				SymbolsPlugin.SYMBOL_GROUP_IMAGE_PATH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IDataProvider) {
			return ((IDataProvider) element).getName();
		}
		return super.getText(element);
	}
}
