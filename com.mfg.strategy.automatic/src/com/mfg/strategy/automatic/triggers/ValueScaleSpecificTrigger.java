/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.triggers;

import com.mfg.widget.priv.TRIGGER_TYPE;

public abstract class ValueScaleSpecificTrigger extends ScaleSpecificTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 924526841705117280L;
	protected TRIGGER_TYPE fValueType = TRIGGER_TYPE.PRICE;


	public ValueScaleSpecificTrigger() {
		super();
	}


	public ValueScaleSpecificTrigger(TRIGGER_TYPE aValueType) {
		super();
		fValueType = aValueType;
	}


	/**
	 * gets the type of the value.
	 * 
	 * @return the type of the value.
	 */
	// @JSON(index = 200)
	// @Param
	// @Label("Type")
	public TRIGGER_TYPE getValueType() {
		return fValueType;
	}


	/**
	 * sets the type of the value.
	 * 
	 * @param aValueType
	 *            the new type of the value to set.
	 */
	public void setValueType(TRIGGER_TYPE aValueType) {
		fValueType = aValueType;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fValueType == null) ? 0 : fValueType.hashCode());
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueScaleSpecificTrigger other = (ValueScaleSpecificTrigger) obj;
		if (fValueType == null) {
			if (other.fValueType != null)
				return false;
		} else if (!fValueType.equals(other.fValueType))
			return false;
		return true;
	}

}
