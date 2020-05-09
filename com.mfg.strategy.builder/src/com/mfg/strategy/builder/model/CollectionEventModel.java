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

import com.mfg.strategy.automatic.eventPatterns.EventGroup;

public abstract class CollectionEventModel extends EventModelNode {

	private static final long serialVersionUID = 211349823084300990L;


	protected CollectionEventModel() {
		super();
	}


	protected void addChildren(EventGroup res) {
		for (EventModelNode e : this.children) {
			res.addEvent(e.exportMe());
		}
	}


	@Override
	public boolean addChild(EventModelNode aChild) {
		return super.addChild(aChild);
	}


	@Override
	public boolean removeChild(EventModelNode aChild) {
		return super.removeChild(aChild);
	}


	@Override
	public boolean canAdd(EventModelNode aChild) {
		return !(aChild instanceof CommandEventModel);
	}


	@Override
	public boolean isValid() {
		return getChildren().size() > 1;
	}
}
