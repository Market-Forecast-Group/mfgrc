package com.mfg.dm;

/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.mfg.common.BAR_TYPE;

/**
 * @author arian
 * 
 */
public class SlotParams /* extends GenericIdentifier */{

	/**
	 * 
	 */
	private static final String PROP_SCALE = "scale";
	/**
	 * 
	 */
	private static final String PROP_GAP = "gap";
	private static final String PROP_MULTIPLICITY_BAR = "multiplicityBar";
	private static final String PROP_BAR_TYPE = "barType";
	private static final String PROP_NUM_BARS = "numBars";
	private static final String PROP_UNITS_TYPE = "unitsType";

	// @Override
	// protected void _toJsonEmbedded(JSONStringer stringer) throws
	// JSONException {
	// stringer.key("NumBars");
	// stringer.value(this.numBars);
	// stringer.key("BarType");
	// stringer.value(barType);
	// stringer.key(PROP_MULTIPLICITY_BAR);
	// stringer.value(multiplicityBar);
	// }

	private int numBars = 100;
	private BAR_TYPE barType = BAR_TYPE.MINUTE;
	private int multiplicityBar = 1;
	private UnitsType unitsType = UnitsType.DAYS;
	private double gap;

	private transient PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private int scale;
	private int fGap1;
	private int fGap2;
	private long _startDate;

	public SlotParams() {
	}

	public SlotParams(UnitsType unitsType1, int multiplicityBar1,
			BAR_TYPE barType1, int numBars1) {
		this.unitsType = unitsType1;
		this.numBars = numBars1;
		this.barType = barType1;
		this.multiplicityBar = multiplicityBar1;
		_startDate = -1;
	}

	/**
	 * @deprecated You should use {@link ESignalHistoricalDataInfo#getGap1()} or
	 *             {@link ESignalHistoricalDataInfo#getGap2()}.
	 * @return the gap
	 */
	@Deprecated
	public double getGap() {
		return gap;
	}

	/**
	 * @param gap1
	 *            the gap to set
	 */
	@Deprecated
	public void setGap(double gap1) {
		this.gap = gap1;
		firePropertyChange(PROP_GAP);
	}

	/**
	 * @return the unitsType
	 */
	public UnitsType getUnitsType() {
		return unitsType;
	}

	/**
	 * @param unitsType1
	 *            the unitsType to set
	 */
	public void setUnitsType(UnitsType unitsType1) {
		this.unitsType = unitsType1;
		firePropertyChange(PROP_UNITS_TYPE);
	}

	/**
	 * How many bars to request?
	 */
	public int getNumBars() {
		return numBars;
	}

	public void setNumBars(int numBars1) {
		this.numBars = numBars1;
		firePropertyChange(PROP_NUM_BARS);
	}

	public BAR_TYPE getBarType() {
		return barType;
	}

	public void setBarType(BAR_TYPE barType1) {
		this.barType = barType1;
		firePropertyChange(PROP_BAR_TYPE);
	}

	/**
	 * How many minutes, how many ranges...
	 */
	public int getMultiplicityBar() {
		return multiplicityBar;
	}

	public void setMultiplicityBar(int multiplicityBar1) {
		this.multiplicityBar = multiplicityBar1;
		firePropertyChange(PROP_MULTIPLICITY_BAR);
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

	// @Override
	// protected void _updateFromJSON(JSONObject json) throws JSONException {
	// assert (false);
	// }

	public int getScale() {
		return scale;
	}

	public void setScale(int scale1) {
		this.scale = scale1;
		firePropertyChange(PROP_SCALE);
	}

	public void setGap1(int gap1) {
		this.fGap1 = gap1;
	}

	public void setGap2(int gap2) {
		this.fGap2 = gap2;
	}

	public int getGap1() {
		return fGap1;
	}

	public int getGap2() {
		return fGap2;
	}

	/**
	 * 
	 * @return the start date for this slot or -1 if start date is not available.
	 */
	public long getStartDate() {
		return _startDate;
	}

	public void setStartDate(long startDate) {
		_startDate = startDate;
	}
}
