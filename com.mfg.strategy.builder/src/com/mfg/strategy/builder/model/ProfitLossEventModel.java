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

import com.mfg.strategy.automatic.eventPatterns.EventAtomProfitLoss;
import com.mfg.strategy.automatic.eventPatterns.EventAtomProfitLoss.ProfitLoss;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.LSFilterType;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class ProfitLossEventModel extends LimitedEventModel implements IBasedOnEntries {

	public enum ProfitType {
		Profit, Loss;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;

	private boolean consideringQ;
	private boolean averagingGain;
	private int tickTH =1;
	private LSFilterType filterType = LSFilterType.Auto;
	private ProfitLoss type = ProfitLoss.Profit;
	private int[] entries = new int[0];
	private boolean relative;

	@Override
	public String getLabel() {
		return tickTH+(isRelative()?"R":"")+" "+type.toString() + getRest();
	}


	private String getRest() {
		return (entries.length == 0 ? "*" : (" of " + Arrays.toString(entries))) + (filterType == LSFilterType.Auto ? "" : (" " + filterType));
	}


	/**
	 * @return the consideringQ
	 */
	public boolean isConsideringQ() {
		return consideringQ;
	}


	/**
	 * @return the averagingGain
	 */
	public boolean isAveragingGain() {
		return averagingGain;
	}


	/**
	 * @return the tickTH
	 */
	public int getTickTH() {
		return tickTH;
	}


	/**
	 * @return the type
	 */
	public ProfitLoss getType() {
		return type;
	}


	/**
	 * @return the entries
	 */
	@Override
	public int[] getEntries() {
		return entries;
	}


	@Override
	public LSFilterType getFilterType() {
		return filterType;
	}


	/**
	 * @param aConsideringQ
	 *            the consideringQ to set
	 */
	public void setConsideringQ(boolean aConsideringQ) {
		if (this.consideringQ != aConsideringQ) {
			this.consideringQ = aConsideringQ;
			firePropertyChange(PropertiesID.PROPERTY_CONSIDERINGQ, null, Boolean.valueOf(aConsideringQ));
		}
	}


	/**
	 * @param aAveragingGain
	 *            the averagingGain to set
	 */
	public void setAveragingGain(boolean aAveragingGain) {
		if (this.averagingGain != aAveragingGain) {
			this.averagingGain = aAveragingGain;
			firePropertyChange(PropertiesID.PROPERTY_AVERAGINGGAIN, null, Boolean.valueOf(aAveragingGain));
		}
	}


	/**
	 * @param aTickTH
	 *            the tickTH to set
	 */
	public void setTickTH(int aTickTH) {
		if (tickTH != aTickTH) {
			tickTH = aTickTH;
			firePropertyChange(PropertiesID.PROPERTY_TICKSTH, null, Integer.valueOf(aTickTH));
		}
	}


	/**
	 * @param aType
	 *            the type to set
	 */
	public void setType(ProfitLoss aType) {
		if (type != aType) {
			type = aType;
			firePropertyChange(PropertiesID.PROPERTY_PLTYPE, null, aType);
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
	
	


	public boolean isRelative() {
		return relative;
	}


	public void setRelative(boolean aRelative) {
		if (this.relative != aRelative) {
			this.relative = aRelative;
			firePropertyChange(PropertiesID.PROPERTY_RELATIVE, null, Boolean.valueOf(aRelative));
		}
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
	public EventGeneral exportMe() {
		EventAtomProfitLoss res = new EventAtomProfitLoss();
		res.setBasedOn(getFilterType());
		res.setEntries(getEntries());
		res.setConsideringQ(isConsideringQ());
		res.setAveragingGain(isAveragingGain());
		res.setLimitToSwingZero(isLimitedToSwing0());
		res.setTicksTH(getTickTH());
		res.setType(getType());
		res.setRelative(isRelative());
		return res;
	}

}
