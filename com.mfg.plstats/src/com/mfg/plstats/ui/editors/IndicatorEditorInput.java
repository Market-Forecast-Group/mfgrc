/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.plstats.ui.editors;

import java.io.File;
import java.util.UUID;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.plstats.IndicatorEditorInputFactory;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.persist.PLStatsIndicatorStorage;
import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * @author arian
 * 
 */
public class IndicatorEditorInput implements IEditorInput, IPersistableElement {

	public static final String MEMENTO_INDICATOR_UUID = "IndicatorEditorInput.configurationUUID";

	private final IIndicatorConfiguration _configuration;

	/**
	 * 
	 */
	public IndicatorEditorInput(final IIndicatorConfiguration aConfiguration) {
		this._configuration = aConfiguration;
	}

	/**
	 * @return the configuration
	 */
	public IIndicatorConfiguration getConfiguration() {
		return _configuration;
	}

	public IndicatorParamBean getIndicatorSettings() {
		return (IndicatorParamBean) getConfiguration().getIndicatorSettings();
	}

	public Configuration getProbabilitiesSettings() {
		return getConfiguration().getProbabilitiesSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		PLStatsIndicatorStorage storage = PLStatsPlugin.getDefault()
				.getIndicatorStorage();
		return storage.isPersisted(_configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return getConfiguration().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		String name = getConfiguration().getName();
		CSVSymbolData symbol = getConfiguration().getSymbol();
		File file = symbol.getFile();
		String filename = file.getName();
		return name + " - " + filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof IndicatorEditorInput
				&& ((IndicatorEditorInput) obj).getConfiguration() == _configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		UUID uuid = _configuration.getUUID();
		String value = uuid.toString();
		memento.putString(MEMENTO_INDICATOR_UUID, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPersistableElement#getFactoryId()
	 */
	@Override
	public String getFactoryId() {
		return IndicatorEditorInputFactory.ID;
	}

	@Override
	public int hashCode() {
		return getConfiguration().hashCode();
	}
}
