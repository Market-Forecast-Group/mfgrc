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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;

/**
 * Job manager wrapper to the Eclipse job manager. This manager should be used
 * to get reports of the other symbols job events like the trading stop, start,
 * pause and restart.
 * 
 * @author arian
 * 
 */
public class SymbolJobManager {
	static final int TRADING_START = 0;
	static final int TRADING_STOPPED = 1;
	static final int TRADING_PAUSED = 2;
	static final int TRADING_RESTARTED = 3;
	static final int WARMING_UP_FINISHED = 4;

	private final List<ISymbolJobChangeListener> list;

	SymbolJobManager() {
		list = new ArrayList<>();
	}

	public void addJobChangeListener(ISymbolJobChangeListener listener) {
		synchronized (list) {
			list.add(listener);
			Job.getJobManager().addJobChangeListener(listener);
		}
	}

	public void removeJobChangeListener(ISymbolJobChangeListener listener) {
		synchronized (list) {
			list.remove(listener);
			Job.getJobManager().removeJobChangeListener(listener);
		}
	}

	void fireSymbolJobChange(SymbolJob<?> job, TradingPipe pipe, int change) {
		for (ISymbolJobChangeListener l : list
				.toArray(new ISymbolJobChangeListener[list.size()])) {
			switch (change) {
			case TRADING_START:
				l.tradingStarted(new TradingPipeChangeEvent(pipe));
				break;
			case TRADING_STOPPED:
				l.tradingStopped(new TradingPipeChangeEvent(pipe));
				break;
			case TRADING_PAUSED:
				l.tradingPaused(new TradingPipeChangeEvent(pipe));
				break;
			case TRADING_RESTARTED:
				l.tradingRestarted(new TradingPipeChangeEvent(pipe));
				break;
			case WARMING_UP_FINISHED:
				l.warmingUpFinished(new SymbolJobChangeEvent(job));
				break;
			}
		}
	}

	/**
	 * @param symbolJob
	 */
	public void fireInputPipeStopped(SymbolJob<?> symbolJob, InputPipe inputPipe) {
		synchronized (list) {
			for (ISymbolJobChangeListener l : list) {
				l.inputStopped(new InputPipeChangeEvent(inputPipe));
			}
		}
	}
}
