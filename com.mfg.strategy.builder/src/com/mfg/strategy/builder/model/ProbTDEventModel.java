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

import com.mfg.strategy.automatic.eventPatterns.EventAtomProbTD;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class ProbTDEventModel extends ScaledEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5721332646191851598L;

	private double fTDTH = -1;


	@Override
	public String getLabel() {
		return "ProbTD{scale=" + widgetScale + thTDText() + "}";
	}


	private String thTDText() {
		if (fTDTH >= 0)
			return ", TD >=" + fTDTH;
		return "";
	}


	public double getTDTH() {
		return fTDTH;
	}


	public void setTDTH(double aTDTH) {
		if (fTDTH != aTDTH) {
			fTDTH = aTDTH;
			firePropertyChange(PropertiesID.PROPERTY_TDTH, null, Double.valueOf(aTDTH));
		}
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomProbTD res = new EventAtomProbTD();
		res.setWidgetScale(getWidgetScale());
		res.setTDTH(getTDTH());
		return res;
	}


	@Override
	public boolean isProbabilistic() {
		return true;
	}

}
