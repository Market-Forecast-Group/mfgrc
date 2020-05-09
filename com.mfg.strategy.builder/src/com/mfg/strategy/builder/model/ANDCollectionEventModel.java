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

import com.mfg.strategy.automatic.eventPatterns.EventANDCollection;

public class ANDCollectionEventModel extends CollectionEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String getLabel() {
		return "AND";
	}


	@Override
	public EventANDCollection exportMe() {
		EventANDCollection res = new EventANDCollection();
		addChildren(res);
		return res;
	}

}
