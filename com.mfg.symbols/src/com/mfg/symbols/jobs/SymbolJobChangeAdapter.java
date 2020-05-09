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
package com.mfg.symbols.jobs;

import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author arian
 * 
 */
public class SymbolJobChangeAdapter extends JobChangeAdapter implements
		ISymbolJobChangeListener {

	@Override
	public void tradingStarted(TradingPipeChangeEvent event) {
		tradingChanged(event);
	}

	@Override
	public void tradingStopped(TradingPipeChangeEvent event) {
		tradingChanged(event);
	}

	@Override
	public void tradingPaused(TradingPipeChangeEvent event) {
		tradingChanged(event);
	}

	@Override
	public void tradingRestarted(TradingPipeChangeEvent event) {
		tradingChanged(event);
	}

	/**
	 * @param event  
	 */
	public void tradingChanged(TradingPipeChangeEvent event) {
		//Adding a comment to avoid empty block warning.
	}

	@Override
	public void warmingUpFinished(SymbolJobChangeEvent event) {
		//Adding a comment to avoid empty block warning.
	}

	@Override
	public void inputStopped(InputPipeChangeEvent event) {
		//Adding a comment to avoid empty block warning.
	}

}
