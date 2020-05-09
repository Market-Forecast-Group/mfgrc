/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.plstats.ui.actions;

import org.eclipse.jface.action.Action;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;

/**
 * @author arian
 * 
 */
public class AbstractIndicatorAction extends Action {
	private IIndicatorConfiguration configuration;


	public AbstractIndicatorAction(String text, IIndicatorConfiguration aConfiguration) {
		super(text);
		this.configuration = aConfiguration;
	}


	/**
	 * @return the configuration
	 */
	public IIndicatorConfiguration getConfiguration() {
		return configuration;
	}
}
