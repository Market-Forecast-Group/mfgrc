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

import com.mfg.strategy.automatic.eventPatterns.EventAtomTHRelationShip;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;

public class THRelEventModel extends LimitedEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;


	@Override
	public String getLabel() {
		return "TH Rel{" + ((!isLimitedToSwing0()) ? "" : (", on Sw0 ")) + "}";
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomTHRelationShip res = new EventAtomTHRelationShip();
		// res.setLimitToSwingZero(isLimitedToSwing0());
		// THRelationShipTrigger par = new THRelationShipTrigger();
		// par.setRelationshipType(get)
		// res.setTHRelationShipTrigger(par);
		return res;
	}
}
