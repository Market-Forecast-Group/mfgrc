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

package com.mfg.widget;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.ChannelIndicator;

/**
 * @author arian
 * 
 */
@XmlRootElement(name = "IndicatorConfiguration")
@XmlSeeAlso(Configuration.class)
public class IndicatorConfiguration implements IIndicatorConfiguration,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String probabilityName = "New Probability";

	private transient PropertyChangeSupport support;

	private CSVSymbolData symbol;

	private AbstractIndicatorParamBean settings;

	private transient ChartMDBSession mdbSession;

	private Configuration configuration;

	private UUID uuid;

	private transient PropertyChangeListener settingsListener;

	public IndicatorConfiguration() {
		settingsListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange("indicatorSettings");
			}
		};
		support = new PropertyChangeSupport(this);
	}

	public IndicatorConfiguration(String name1) {
		this();
		settings = new IndicatorParamBean();
		settings.addPropertyChangeListener(settingsListener);
		configuration = new Configuration();
		configuration.fixStartScale(ChannelIndicator.START_SCALE_LEVEL);
		configuration.fixEndScale(settings.getIndicatorNumberOfScales());
		this.name = name1;
		uuid = UUID.randomUUID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.symbols.IIndicatorConfiguration#getUUID()
	 */
	@Override
	public UUID getUUID() {
		return uuid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.interfaces.symbols.IIndicatorConfiguration#setUUID(java.util.
	 * UUID)
	 */
	@Override
	public void setUUID(UUID uuid1) {
		this.uuid = uuid1;
	}

	@Override
	@XmlElement
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name1) {
		this.name = name1;
		firePropertyChange("name");
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	@Override
	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	@Override
	@XmlElement
	public AbstractIndicatorParamBean getIndicatorSettings() {
		return settings;
	}

	/**
	 * @param settings1
	 *            the settings to set
	 */
	@Override
	public void setIndicatorSettings(AbstractIndicatorParamBean settings1) {
		if (this.settings != null) {
			this.settings.removePropertyChangeListener(settingsListener);
		}
		this.settings = settings1;
		this.settings.addPropertyChangeListener(settingsListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.symbols.IIndicatorConfiguration#getSymbol()
	 */
	@Override
	@XmlIDREF
	public CSVSymbolData getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol1
	 *            the symbol to set
	 */
	@Override
	public void setSymbol(CSVSymbolData symbol1) {
		this.symbol = symbol1;
	}

	@Override
	@XmlTransient
	public ChartMDBSession getChartDBSession() {
		return mdbSession;
	}

	// TODO: transient for now, we need to persist this
	@Override
	public Configuration getProbabilitiesSettings() {
		return configuration;
	}

	@Override
	public void setProbabilitiesSettings(Configuration aConfiguration) {
		configuration = aConfiguration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result
		// + ((configuration == null) ? 0 : configuration.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((settings == null) ? 0 : settings.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndicatorConfiguration other = (IndicatorConfiguration) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (settings == null) {
			if (other.settings != null)
				return false;
		} else if (!settings.equals(other.settings))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String getProbabilityName() {
		return probabilityName;
	}

	@Override
	public void setProbabilityName(String aProbabilityName) {
		probabilityName = aProbabilityName;
	}

}
