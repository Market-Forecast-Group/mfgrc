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

import java.util.List;

public abstract class SimpleEventModel extends EventModelNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public SimpleEventModel() {
		super();
		// setMyEvent(new EventAtomTH());
	}


	@Override
	public boolean canAdd(EventModelNode aChild) {
		return false;
	}


	@Override
	public boolean isCollapsed() {
		return true;
	}


	@Override
	public String getLabel() {
		return "TH{5,3}";
	}


	@Override
	public void setChildren(List<EventModelNode> aChildren) {
		throw new UnsupportedOperationException("No children allowed");
	}


	@Override
	public boolean addChild(EventModelNode aChild) {
		throw new UnsupportedOperationException("No children allowed");
	}


	@Override
	public void addChild(int aIndex, EventModelNode aChild) {
		throw new UnsupportedOperationException("No children allowed");
	}
	
	@Override
	public EventModelNode clone() {
		return super.clone();
	}

}
