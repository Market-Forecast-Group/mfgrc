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

import com.mfg.strategy.automatic.eventPatterns.EventAtomCommand;
import com.mfg.strategy.automatic.eventPatterns.EventCommandContainer;

public class ConditionalCommandEventModel extends EventModelNode {

	private static final long serialVersionUID = 211349823084300990L;


	public ConditionalCommandEventModel() {
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
		return getChildren().size() < 2 && ((aChild instanceof CommandEventModel && !hasCMD()) || (!(aChild instanceof CommandEventModel) && !hasCND()));
	}


	@Override
	public boolean isValid() {
		return getChildren().size() == 2 && hasCMD() && hasCND();
	}


	private boolean hasCND() {
		for (Object e : getChildren()) {
			if (!(e instanceof CommandEventModel))
				return true;
		}
		return false;
	}


	private boolean hasCMD() {
		for (Object e : getChildren()) {
			if (e instanceof CommandEventModel)
				return true;
		}
		return false;
	}


	private EventModelNode getSon(boolean cmd) {
		for (EventModelNode e : getChildren()) {
			if (e instanceof CommandEventModel == cmd)
				return e;
		}
		return null;
	}


	@Override
	public String getLabel() {
		return "CND CMD";
	}


	@Override
	public EventCommandContainer exportMe() {
		EventCommandContainer res = new EventCommandContainer();
		res.setCommand((EventAtomCommand) getSon(true).exportMe());
		res.setPrecondition(getSon(false).exportMe());
		return res;
	}
}
