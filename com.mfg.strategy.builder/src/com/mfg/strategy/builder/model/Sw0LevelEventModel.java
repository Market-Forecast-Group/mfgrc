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

import com.mfg.strategy.automatic.eventPatterns.EventAtomSw0Level;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.triggers.NewValueLevelTrigger;
import com.mfg.strategy.builder.model.psource.PropertiesID;
import com.mfg.widget.priv.StartPoint;

public class Sw0LevelEventModel extends ScaledEventModel {

//	public enum StartPoint {
//		P0, Pm1, HHLL
//	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;

	private boolean updatingReferences;
	private double percent = 1;
	private int refSwing = -1;
	private StartPoint startPoint = StartPoint.P_0;


	@Override
	public String getLabel() {
		return "Sw0 Level{" + "scale=" + getWidgetScale() + ((!isLimitedToSwing0()) ? "" : (", on Sw0 ")) + "}";
	}


	/**
	 * @return the updatingReferences
	 */
	public boolean isUpdatingReferences() {
		return updatingReferences;
	}


	/**
	 * @return the percent
	 */
	public double getPercent() {
		return percent;
	}


	/**
	 * @return the refSwing
	 */
	public int getRefSwing() {
		return refSwing;
	}


	/**
	 * @return the startPoint
	 */
	public StartPoint getStartPoint() {
		return startPoint;
	}


	/**
	 * @param aUpdatingReferences
	 *            the updatingReferences to set
	 */
	public void setUpdatingReferences(boolean aUpdatingReferences) {
		if (updatingReferences != aUpdatingReferences) {
			updatingReferences = aUpdatingReferences;
			firePropertyChange(PropertiesID.PROPERTY_UPDATINGREF, null, Boolean.valueOf(aUpdatingReferences));
		}
	}


	/**
	 * @param aPercent
	 *            the percent to set
	 */
	public void setPercent(double aPercent) {
		if (percent != aPercent) {
			percent = aPercent;
			firePropertyChange(PropertiesID.PROPERTY_PERCENT, null, Double.valueOf(aPercent));
		}
	}


	/**
	 * @param aRefSwing
	 *            the refSwing to set
	 */
	public void setRefSwing(int aRefSwing) {
		if (refSwing != aRefSwing) {
			refSwing = aRefSwing;
			firePropertyChange(PropertiesID.PROPERTY_REFSWING, null, Integer.valueOf(aRefSwing));
		}
	}


	/**
	 * @param aStartPoint
	 *            the startPoint to set
	 */
	public void setStartPoint(StartPoint aStartPoint) {
		if (startPoint != aStartPoint) {
			startPoint = aStartPoint;
			firePropertyChange(PropertiesID.PROPERTY_REFSWING, null, aStartPoint);
		}
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomSw0Level res = new EventAtomSw0Level();
		res.setLimitToSwingZero(isLimitedToSwing0());
		NewValueLevelTrigger par = new NewValueLevelTrigger();
		par.setPercent(getPercent());
		par.setWidgetScale(getWidgetScale());
		par.setRefSwing(getRefSwing());
		par.setRefSwing(getRefSwing());
		par.setStartPoint(getStartPoint());
		par.setUpdatingReferences(isUpdatingReferences());
		res.setTrigger(par);
		return res;
	}

}
