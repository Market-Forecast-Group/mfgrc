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
package com.mfg.dm.symbols;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

/**
 * Common data for any symbol in the system. This class is a replacement to the
 * {@link SymbolData} class.
 * 
 * @author arian
 * 
 */
public abstract class SymbolData2 {
	public static final String PROP_REAL_TICK_SIZE = "realTickSize";
	public static final String PROP_AUTO_VERIFY_TICK_INFO = "autoVerifyTickInfo";
	public static final String PROP_LOCAL_SYMBOL = "localSymbol";
	public static final String PROP_NAME = "name";
	public static final String PROP_CURRENCY = "currency";
	public static final String PROP_TICK_VALUE = "tickValue";
	public static final String PROP_TICK_SCALE = "tickScale";
	public static final String PROP_TICK_SIZE = "tickSize";
	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_EUR = "EUR";
	public static final String[] CURRENCIES = { CURRENCY_USD, CURRENCY_EUR };

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	private String localSymbol;
	private Integer tickSize;
	private BigDecimal realTickSize;
	private Integer tickScale;
	/**
	 * The tick value is how much cents are worth a movement of one tick of this
	 * instrument. The tick value is an integer, because it is an exact amount
	 * of cents.
	 */
	private int tickValueInt;

	/**
	 * Used only for migration purpose of the xstream serialization.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private double tickValue;
	private String currency;
	private boolean autoVerifyTickInfo;

	public SymbolData2() {
		tickSize = null;
		tickScale = null;
		currency = CURRENCY_USD;
		autoVerifyTickInfo = false;
	}

	public String getLocalSymbol() {
		return localSymbol;
	}

	public void setLocalSymbol(String localSymbol1) {
		this.localSymbol = localSymbol1;
		firePropertyChange(PROP_LOCAL_SYMBOL);
	}

	/**
	 * The tick value is how much cents are worth a movement of one tick of this
	 * instrument. The tick value is an integer, because it is an exact amount
	 * of cents (in case the scale is 2).
	 */
	public int getTickValue() {
		return tickValueInt;
	}

	public void setTickValue(int tickValue1) {
		tickValueInt = tickValue1;
	}
	
	public double getRealTickValue() {
		return tickValueInt / Math.pow(10, getTickScale().intValue());
	}

	/**
	 * The real symbol's tick size. For example, if you put a 0.01 value, the
	 * tick size will get the value 1 and the tick scale 2.
	 * 
	 * @return the tickSize2
	 */
	public BigDecimal getRealTickSize() {
		return realTickSize;
	}

	/**
	 * The real symbol's tick size. It is computed automatically if you keep it
	 * <code>null</code>.
	 * 
	 * @param realTickSize1
	 *            the tickSize2 to set
	 */
	@SuppressWarnings("boxing")
	public void setRealTickSize(BigDecimal realTickSize1) {
		this.realTickSize = realTickSize1;
		if (realTickSize1 == null) {
			setTickSize(null);
			setTickScale(null);
		} else {
			setTickScale(realTickSize1.scale());
			setTickSize(realTickSize1.unscaledValue().intValue());
		}
		firePropertyChange(PROP_REAL_TICK_SIZE);
	}

	/**
	 * Symbol's tick size. It is computed automatically if you keep it
	 * <code>null</code>.
	 * 
	 * @return Tick size, maybe <code>null</code>.
	 */
	public Integer getTickSize() {
		return tickSize;
	}

	public void setTickSize(Integer tickSize1) {
		this.tickSize = tickSize1;
		firePropertyChange(PROP_TICK_SIZE);
	}

	/**
	 * Symbol's tick scale. It is computed automatically if you keep it
	 * <code>null</code>.
	 * 
	 * @return Tick scale, maybe <code>null</code>.
	 */
	public Integer getTickScale() {
		return tickScale;
	}

	public void setTickScale(Integer tickScale1) {
		this.tickScale = tickScale1;
		firePropertyChange(PROP_TICK_SCALE);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency1) {
		this.currency = currency1;
		firePropertyChange(PROP_CURRENCY);
	}

	public boolean isAutoVerifyTickInfo() {
		return autoVerifyTickInfo;
	}

	public void setAutoVerifyTickInfo(boolean autoVerifyTickInfo1) {
		this.autoVerifyTickInfo = autoVerifyTickInfo1;
		firePropertyChange(PROP_AUTO_VERIFY_TICK_INFO);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}
}
