package com.mfg.symbols.inputs.ui.views;

import org.eclipse.swt.graphics.Image;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.ui.views.StorageObjectsLabelProvider;
import com.mfg.utils.ImageUtils;

public class InputsLabelProvider extends StorageObjectsLabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof InputConfiguration) {
			return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
					SymbolsPlugin.INPUT_IMAGE_PATH);
		}
		return super.getImage(element);
	}
}
