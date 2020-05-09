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

import com.mfg.strategy.automatic.eventPatterns.LSFilterType;

public interface IBasedOnEntries {

	/**
	 * @return the entries
	 */
	public abstract int[] getEntries();


	/**
	 * @return the filterType
	 */
	public abstract LSFilterType getFilterType();


	/**
	 * @param aEntries
	 *            the entries to set
	 */
	public abstract void setEntries(int[] aEntries);


	/**
	 * @param aFilterType
	 *            the filterType to set
	 */
	public abstract void setFilterType(LSFilterType aFilterType);

}
