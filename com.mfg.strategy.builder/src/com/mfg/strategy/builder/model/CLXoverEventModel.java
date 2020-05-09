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

import com.mfg.strategy.automatic.eventPatterns.EventAtomCLXover;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.triggers.CLXoverTrigger;
import com.mfg.strategy.builder.model.psource.PropertiesID;

public class CLXoverEventModel extends ScaledEventModel implements IContrarian {

	/**
	 * 
	 */
	private static final long serialVersionUID = 94798307491043462L;

	private boolean contrarian;


	@Override
	public String getLabel() {
		return "CL XOVER{scale=" + widgetScale + ((!isLimitedToSwing0()) ? "" : (", on Sw0 ")) + "}";
	}


	/**
	 * @return the contrarian
	 */
	@Override
	public boolean isContrarian() {
		return contrarian;
	}


	/**
	 * @param aContrarian
	 *            the contrarian to set
	 */
	@Override
	public void setContrarian(boolean aContrarian) {
		if (contrarian != aContrarian) {
			contrarian = aContrarian;
			firePropertyChange(PropertiesID.PROPERTY_CONTRARIAN, null, Boolean.valueOf(aContrarian));
		}
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomCLXover res = new EventAtomCLXover();
		res.setLimitToSwingZero(isLimitedToSwing0());
		CLXoverTrigger par = new CLXoverTrigger();
		par.setContrarian(isContrarian());
		par.setWidgetScale(getWidgetScale());
		res.setTrigger(par);
		return res;
	}

}
