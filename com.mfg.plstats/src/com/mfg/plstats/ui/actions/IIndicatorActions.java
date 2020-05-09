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

/**
 * @author arian
 * 
 */
public interface IIndicatorActions {

	public abstract Action getCreateIndicatorAction();

	public abstract Action getCreateProbabilitiesAction();
	
	public Action getExportIndicatorAction();

	public abstract Action getLoadIndicatorAction();

	public abstract Action getIndexAction();

	public abstract Action getOpenIndicatorChartAction();

	public abstract void dispose();

}
