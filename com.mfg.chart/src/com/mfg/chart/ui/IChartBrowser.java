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
package com.mfg.chart.ui;

/**
 * @author arian
 * 
 */
public interface IChartBrowser {
	public boolean isActive();

	public boolean hasNext();

	public boolean hasPrev();

	public void moveNext();

	public void movePrev();

	public long getCurrentTime();

	public double getCurrentPrice();
}
