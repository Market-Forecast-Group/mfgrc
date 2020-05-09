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

import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.NotEvent;

public class NOTEventModel extends EventModelNode {

	private static final long serialVersionUID = 211349823084300990L;


	public NOTEventModel() {
		super();
	}


	@Override
	public boolean addChild(EventModelNode aChild) {
		if (canAdd(aChild))
			return super.addChild(aChild);
		return false;
	}


	@Override
	public boolean removeChild(EventModelNode aChild) {
		return super.removeChild(aChild);
	}


	@Override
	public boolean canAdd(EventModelNode aChild) {
		return getChildren().size() < 1 && !(aChild instanceof CommandEventModel);
	}


	@Override
	public boolean isValid() {
		return getChildren().size() == 1;
	}


	@Override
	public String getLabel() {
		return "NOT";
	}


	@Override
	public EventGeneral exportMe() {
		NotEvent res = new NotEvent();
		res.setEvent(getChildren().get(0).exportMe());
		return res;
	}
}
