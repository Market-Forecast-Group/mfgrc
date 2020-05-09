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

import com.mfg.strategy.automatic.eventPatterns.EventAtomCNC;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class CNCEventModel extends LimitedEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;

	private int[] contrarianScales = new int[0];
	private int[] nonContrarianScales = new int[0];


	@Override
	public String getLabel() {
		String res = "C/NC{" + ((!isLimitedToSwing0()) ? "" : ("on Sw0"));
		if (hasContr()) {
			if (isLimitedToSwing0())
				res = res + ", ";
			res = res + ("C=" + Arrays.toString(contrarianScales));
		}
		if (hasNContr()) {
			if (hasContr() || isLimitedToSwing0())
				res = res + ", ";
			res = res + ("NC=" + Arrays.toString(nonContrarianScales));
		}
		return res + "}";
	}


	private boolean hasNContr() {
		return nonContrarianScales != null && nonContrarianScales.length > 0;
	}


	private boolean hasContr() {
		return contrarianScales != null && contrarianScales.length > 0;
	}


	/**
	 * @return the contrarianScales
	 */
	public int[] getContrarianScales() {
		return contrarianScales;
	}


	/**
	 * @param aContrarianScales
	 *            the contrarianScales to set
	 */
	public void setContrarianScales(int[] aContrarianScales) {
		contrarianScales = aContrarianScales;
		firePropertyChange(PropertiesID.PROPERTY_CSCARRAY, null, aContrarianScales);
	}


	/**
	 * @return the nonContrarianScales
	 */
	public int[] getNonContrarianScales() {
		return nonContrarianScales;
	}


	/**
	 * @param aNonContrarianScales
	 *            the nonContrarianScales to set
	 */
	public void setNonContrarianScales(int[] aNonContrarianScales) {
		nonContrarianScales = aNonContrarianScales;
		firePropertyChange(PropertiesID.PROPERTY_NCSCARRAY, null, aNonContrarianScales);
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomCNC res = new EventAtomCNC();
		// TODO res.setBasedOn();
		res.setContrarianScales(getContrarianScales());
		res.setNonContrarianScales(getNonContrarianScales());
		res.setLimitToSwingZero(isLimitedToSwing0());
		return res;
	}

}
