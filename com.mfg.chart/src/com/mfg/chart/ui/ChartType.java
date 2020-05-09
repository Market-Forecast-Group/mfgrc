/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.chart.ui;

public enum ChartType {
	TRADING, FINANCIAL, INDICATOR, SYNTHETIC, EQUITY, EMPTY;

	public boolean hasChannels() {
		return this == TRADING || this == INDICATOR;
	}

	public boolean hasExecutions() {
		return this == TRADING;
	}

	public boolean hasProbs() {
		return hasExecutions() || this == INDICATOR;
	}

	public boolean hasEquity() {
		return this == EQUITY;
	}

	public boolean hasPrices() {
		return this != EQUITY && this != SYNTHETIC;
	}

}
