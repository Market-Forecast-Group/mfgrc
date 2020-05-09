package com.mfg.plstats;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.plstats.persist.PLStatsIndicatorStorage;
import com.mfg.plstats.ui.editors.IndicatorEditorInput;

public class IndicatorEditorInputFactory implements IElementFactory {
	public static final String ID = "com.mfg.plstats.IndicatorEditorInputFactory";

	@Override
	public IAdaptable createElement(IMemento memento) {
		if (memento != null) {
			String uuid = memento
					.getString(IndicatorEditorInput.MEMENTO_INDICATOR_UUID);
			if (uuid != null) {
				PLStatsIndicatorStorage storage = PLStatsPlugin.getDefault()
						.getIndicatorStorage();
				PLStatsIndicatorConfiguration config = storage.findById(uuid);
				if (config != null) {
					IndicatorEditorInput indicatorEditorInput = new IndicatorEditorInput(
							config.getIndicator());
					return indicatorEditorInput;
				}
			}
		}
		return null;
	}

}
