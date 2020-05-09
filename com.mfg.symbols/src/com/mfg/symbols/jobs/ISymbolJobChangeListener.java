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

import org.eclipse.core.runtime.jobs.IJobChangeListener;

/**
 * @author arian
 * 
 */
public interface ISymbolJobChangeListener extends IJobChangeListener {
	public void tradingStarted(TradingPipeChangeEvent event);

	public void tradingStopped(TradingPipeChangeEvent event);

	public void tradingPaused(TradingPipeChangeEvent event);

	public void tradingRestarted(TradingPipeChangeEvent event);

	public void warmingUpFinished(SymbolJobChangeEvent event);

	public void inputStopped(InputPipeChangeEvent event);
}
