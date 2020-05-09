/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.model;

import java.util.Arrays;

import com.mfg.strategy.automatic.eventPatterns.EventAtomExit;
import com.mfg.strategy.automatic.eventPatterns.LSFilterType;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class ExitEventModel extends CommandEventModel implements IBasedOnEntries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8841190060186756946L;

	private boolean usingLimitFamily = true;
	private boolean global;
	private int[] entries = new int[0];
	private LSFilterType filterType = LSFilterType.Auto;


	public ExitEventModel() {
	}


	@Override
	public String getLabel() {
		return "Exit " + getRest();
	}


	private String getRest() {
		return (entries.length == 0 ? "*" : (" of " + Arrays.toString(entries))) + (getFilterType() == LSFilterType.Auto ? "" : (" " + getFilterType().toString().charAt(0)));
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IMarketFamily#isMarketFamily()
	 */
	public boolean isUsingLimitFamily() {
		return usingLimitFamily;
	}


	/**
	 * @return the global
	 */
	public boolean isGlobal() {
		return global;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IBasedOnEntries#getEntries()
	 */
	@Override
	public int[] getEntries() {
		return entries;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IBasedOnEntries#getFilterType()
	 */
	@Override
	public LSFilterType getFilterType() {
		return filterType;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IMarketFamily#setMarketFamily(boolean)
	 */
	public void setUsingLimitFamily(boolean aMarketFamily) {
		if (usingLimitFamily != aMarketFamily) {
			usingLimitFamily = aMarketFamily;
			firePropertyChange(PropertiesID.PROPERTY_LIMITFAMILY, null, Boolean.valueOf(aMarketFamily));
		}
	}


	/**
	 * @param aGlobal
	 *            the global to set
	 */
	public void setGlobal(boolean aGlobal) {
		if (global != aGlobal) {
			global = aGlobal;
			firePropertyChange(PropertiesID.PROPERTY_GLOBAL, null, Boolean.valueOf(aGlobal));
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IBasedOnEntries#setEntries(int[])
	 */
	@Override
	public void setEntries(int[] aEntries) {
		entries = aEntries;
		firePropertyChange(PropertiesID.PROPERTY_ENTRIESARRAYEXIT, null, aEntries);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IBasedOnEntries#setFilterType(com.mfg.strategy.builder.model.FilterType)
	 */
	@Override
	public void setFilterType(LSFilterType aFilterType) {
		if (filterType != aFilterType) {
			filterType = aFilterType;
			firePropertyChange(PropertiesID.PROPERTY_FILTERTYPE, null, aFilterType);
		}
	}


	@Override
	public EventAtomExit exportMe() {
		EventAtomExit res = new EventAtomExit();
		res.setEntries(getEntries());
		res.setFilterType(getFilterType());
		res.setMarketFamily(!isUsingLimitFamily());
		res.setGlobal(isGlobal());
		res.setRequiresConfirmation(isRequiresConfirmation());
		res.setPlaySound(isPlaySound());
		return res;
	}

	// @Override
	// public EventModelNode clone() {
	// ExitEventModel res = (ExitEventModel) super.clone();
	// res.entries = Arrays.copyOf(entries, entries.length);
	// return res;
	// }

}
