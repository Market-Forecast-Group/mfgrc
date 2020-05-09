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

import com.mfg.strategy.automatic.eventPatterns.EventAtomEntry;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class EntryEventModel extends CommandEventModel implements IMarketFamily, IContrarian, ILimitedToSW0 {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7160594520264447123L;

	private int ID = 1;
	private boolean contrarian = true;
	private int quantity = 1;
	private boolean marketFamily = true;
	private boolean usingSL = true;
	private int SLScale = 5;
	private boolean usingLimitChild = false;

	private boolean limitedToSwing0;

	private boolean multipleEntries;
	private int[] singleEntriesScales = new int[0];

	private boolean theProbabilistic = false;

	private boolean linkedToEntry;
	private int entryLinkID;


	@Override
	public String getLabel() {
		return "ENTRY " + ID;
	}


	@Override
	public boolean isMarketFamily() {
		return marketFamily;
	}


	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IContrarian#isContrarian()
	 */
	@Override
	public boolean isContrarian() {
		return contrarian;
	}


	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}


	/**
	 * @return the usingSL
	 */
	public boolean isUsingSL() {
		return usingSL;
	}


	/**
	 * @return the sLScale
	 */
	public int getSLScale() {
		return SLScale;
	}


	/**
	 * @return the usingLimitChild
	 */
	public boolean isUsingLimitChild() {
		return usingLimitChild;
	}


	@Override
	public boolean isProbabilistic() {
		return theProbabilistic;
	}


	@Override
	public void setProbabilistic(boolean aProbabilistic) {
		if (theProbabilistic != aProbabilistic) {
			theProbabilistic = aProbabilistic;
			firePropertyChange(PropertiesID.PROPERTY_PROBABILISTIC, null, Boolean.valueOf(aProbabilistic));
		}
	}


	/**
	 * @param aSLScale
	 *            the sLScale to set
	 */
	public void setSLScale(int aSLScale) {
		if (SLScale != aSLScale) {
			SLScale = aSLScale;
			firePropertyChange(PropertiesID.PROPERTY_SLSCALE, null, Integer.valueOf(aSLScale));
		}
	}


	/**
	 * @param aUsingLimitChild
	 *            the usingLimitChild to set
	 */
	public void setUsingLimitChild(boolean aUsingLimitChild) {
		if (usingLimitChild != aUsingLimitChild) {
			usingLimitChild = aUsingLimitChild;
			firePropertyChange(PropertiesID.PROPERTY_LIMITCHILD, Boolean.valueOf(!aUsingLimitChild), Boolean.valueOf(aUsingLimitChild));
		}
	}


	/**
	 * @param aUsingSL
	 *            the usingSL to set
	 */
	public void setUsingSL(boolean aUsingSL) {
		if (usingSL != aUsingSL) {
			usingSL = aUsingSL;
			firePropertyChange(PropertiesID.PROPERTY_SL, Boolean.valueOf(!aUsingSL), Boolean.valueOf(aUsingSL));
		}
	}


	/**
	 * @param aID
	 *            the iD to set
	 */
	public void setID(int aID) {
		if (ID != aID) {
			ID = aID;
			firePropertyChange(PropertiesID.PROPERTY_ID, null, Integer.valueOf(aID));
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IContrarian#setContrarian(boolean)
	 */
	@Override
	public void setContrarian(boolean aContrarian) {
		if (contrarian != aContrarian) {
			contrarian = aContrarian;
			firePropertyChange(PropertiesID.PROPERTY_CONTRARIAN, null, Boolean.valueOf(aContrarian));
		}
	}


	/**
	 * @param aQuantity
	 *            the quantity to set
	 */
	public void setQuantity(int aQuantity) {
		if (quantity != aQuantity) {
			quantity = aQuantity;
			firePropertyChange(PropertiesID.PROPERTY_QUANTITY, null, Integer.valueOf(aQuantity));
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.strategy.builder.model.IMarketFamily#setMarketFamily(boolean)
	 */
	@Override
	public void setMarketFamily(boolean aMarketFamily) {
		if (marketFamily != aMarketFamily) {
			marketFamily = aMarketFamily;
			firePropertyChange(PropertiesID.PROPERTY_MARKETFAMILY, null, Boolean.valueOf(aMarketFamily));
		}
	}


	@Override
	public boolean isLimitedToSwing0() {
		return limitedToSwing0;
	}


	@Override
	public void setLimitedToSwing0(boolean aLimitedToSwing0) {
		if (limitedToSwing0 != aLimitedToSwing0) {
			limitedToSwing0 = aLimitedToSwing0;
			firePropertyChange(PropertiesID.PROPERTY_LSW0, null, Boolean.valueOf(aLimitedToSwing0));
		}
	}


	public boolean isMultipleEntries() {
		return multipleEntries;
	}


	public void setMultipleEntries(boolean aMultipleEntries) {
		if (this.multipleEntries != aMultipleEntries) {
			this.multipleEntries = aMultipleEntries;
			firePropertyChange(PropertiesID.PROPERTY_MULTIPLEENTRIES, null, Boolean.valueOf(aMultipleEntries));
		}

	}


	public void setLinkedToEntry(boolean aLinkedToEntry) {
		if (this.linkedToEntry != aLinkedToEntry) {
			this.linkedToEntry = aLinkedToEntry;
			firePropertyChange(PropertiesID.PROPERTY_LINKEDTOENTRY, null, Boolean.valueOf(aLinkedToEntry));
		}
	}


	public void setEntryLinkID(int aEntryLinkID) {
		if (this.entryLinkID != aEntryLinkID) {
			this.entryLinkID = aEntryLinkID;
			firePropertyChange(PropertiesID.PROPERTY_ENTRYLINKID, null, Boolean.valueOf(linkedToEntry));
		}
	}


	public boolean isLinkedToEntry() {
		return linkedToEntry;
	}


	public int getEntryLinkID() {
		return entryLinkID;
	}


	/**
	 * @return the singleEntriesScales
	 */
	public int[] getSingleEntriesScales() {
		return singleEntriesScales;
	}


	/**
	 * @param aSingleEntriesScales
	 *            the singleEntriesScales to set
	 */
	public void setSingleEntriesScales(int[] aSingleEntriesScales) {
		singleEntriesScales = aSingleEntriesScales;
		firePropertyChange(PropertiesID.PROPERTY_SINGLEENTRIESSCALES, null, aSingleEntriesScales);
	}


	@Override
	public EventAtomEntry exportMe() {
		EventAtomEntry res = new EventAtomEntry(getID());
		res.setQuantity(getQuantity());
		res.setContrarian(isContrarian());
		res.setProbabilistic(isProbabilistic());
		res.setMarketFamily(isMarketFamily());
		res.setLimitToSwingZero(isLimitedToSwing0());
		res.setIncludingLimitChild(isUsingLimitChild());
		res.setUsingSLSimpleProtection(isUsingSL());
		res.setSimpleProtectionScale(getSLScale());
		res.setMultipleEntries(isMultipleEntries());
		res.setLinkedToEntry(isLinkedToEntry());
		res.setEntryLinkID(getEntryLinkID());
		res.setRequiresConfirmation(isRequiresConfirmation());
		res.setPlaySound(isPlaySound());
		res.setSoundPath(getSoundPath());
		return res;
	}

}
