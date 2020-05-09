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
package com.mfg.connector.csv;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.mfg.dm.symbols.HistoricalDataInfo;

/**
 * @author arian
 * 
 */
public class CSVHistoricalDataInfo extends HistoricalDataInfo {
	private static final String PROP_FILTER_OUT_OF_RANGE_TICKS = "filterOutOfRangeTicks";
	private static final String PROP_MIN_GAP_IN_TICKS = "minGapInTicks";
	private static final String PROP_GAP1 = "gap1";
	private static final String PROP_GAP2 = "gap2";
	private static final String PROP_SCALE = "scale";
	private static final String PROP_GAP_FILLING_TYPE = "gapFillingType";
	public static final String PROP_NUMBER_OF_PRICES = "numberOfPrices";
	public static final String PROP_DP = "dp";
	public static final String PROP_XP = "xp";

	public enum GapFillingType {
		SLIDING_WINDOW("Sliding Window"), TICKS_NUMBER("Ticks Number");

		private String label;

		private GapFillingType(String name) {
			this.label = name;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	private double xp;
	private double dp;
	private boolean _filterOutOfRangeTicks;
	private int _minGapInTicks;
	private int numberOfPrices;
	private GapFillingType gapFillingType;
	private int scale;
	private int gap1;
	private int gap2;

	public CSVHistoricalDataInfo() {
		xp = 0.25;
		dp = 0.25;
		_filterOutOfRangeTicks = true;
		_minGapInTicks = 5;
		numberOfPrices = 100;
		gapFillingType = GapFillingType.TICKS_NUMBER;
		scale = 0;
		gap1 = 3;
		gap2 = 4;
	}

	public int getMinGapInTicks() {
		return _minGapInTicks;
	}

	public void setMinGapInTicks(int minGapInTicks) {
		_minGapInTicks = minGapInTicks;
		firePropertyChange(PROP_MIN_GAP_IN_TICKS);
	}

	public boolean isFilterOutOfRangeTicks() {
		return _filterOutOfRangeTicks;
	}

	public void setFilterOutOfRangeTicks(boolean filterOutOfRangeTicks) {
		_filterOutOfRangeTicks = filterOutOfRangeTicks;
		firePropertyChange(PROP_FILTER_OUT_OF_RANGE_TICKS);
	}

	/**
	 * @return the gap1
	 */
	public int getGap1() {
		return gap1;
	}

	/**
	 * @param gap11
	 *            the gap1 to set
	 */
	public void setGap1(int gap11) {
		this.gap1 = gap11;
		firePropertyChange(PROP_GAP1);
	}

	/**
	 * @return the gap2
	 */
	public int getGap2() {
		return gap2;
	}

	/**
	 * @param gap21
	 *            the gap2 to set
	 */
	public void setGap2(int gap21) {
		this.gap2 = gap21;
		firePropertyChange(PROP_GAP2);
	}

	/**
	 * @return the gapFillingType
	 */
	public GapFillingType getGapFillingType() {
		return gapFillingType;
	}

	/**
	 * @param gapFillingType1
	 *            the gapFillingType to set
	 */
	public void setGapFillingType(GapFillingType gapFillingType1) {
		this.gapFillingType = gapFillingType1;
		firePropertyChange(PROP_GAP_FILLING_TYPE);
	}

	/**
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @param scale1
	 *            the scale to set
	 */
	public void setScale(int scale1) {
		this.scale = scale1;
		firePropertyChange(PROP_SCALE);
	}

	/**
	 * @return the xp
	 */
	public double getXp() {
		return xp;
	}

	/**
	 * @param xp1
	 *            the xp to set
	 */
	public void setXp(double xp1) {
		this.xp = xp1;
		firePropertyChange(PROP_XP);
	}

	/**
	 * @return the dp
	 */
	public double getDp() {
		return dp;
	}

	/**
	 * @param dp1
	 *            the dp to set
	 */
	public void setDp(double dp1) {
		this.dp = dp1;
		firePropertyChange(PROP_DP);
	}

	/**
	 * @return the numberOfPrices
	 */
	public int getNumberOfPrices() {
		return numberOfPrices;
	}

	/**
	 * @param numberOfPrices1
	 *            the numberOfPrices to set
	 */
	public void setNumberOfPrices(int numberOfPrices1) {
		this.numberOfPrices = numberOfPrices1;
		firePropertyChange(PROP_NUMBER_OF_PRICES);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

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

	@Override
	public boolean allowPaperTrading() {
		return false;
	}

	@Override
	public boolean forceDoPaperTrading() {
		return false;
	}
}
