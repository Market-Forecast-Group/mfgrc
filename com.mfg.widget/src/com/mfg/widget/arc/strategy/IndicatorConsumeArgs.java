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
package com.mfg.widget.arc.strategy;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;

/**
 * @author arian
 * 
 */
public class IndicatorConsumeArgs {
	private final IIndicator indicator;
	private final QueueTick tick;

	public IndicatorConsumeArgs(IIndicator indicator1, QueueTick tick1) {
		super();
		this.indicator = indicator1;
		this.tick = tick1;
	}

	public IIndicator getIndicator() {
		return indicator;
	}

	public QueueTick getTick() {
		return tick;
	}

}
