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
package com.mfg.symbols.trading.ui.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.mfg.logger.ILoggerManager;
import com.mfg.logger.memory.MemoryLoggerManager;
import com.mfg.logger.ui.LogViewerManager;

/**
 * @author arian
 * 
 */
public class LongShort_AM_TabComposite extends Composite {

	/**
	 * 
	 */
	private static final String STRATEGY_LOGGER_LONG_SHORT_ID = "com.mfg.symbols.trading.ui.loggerViewerLongShort";
	private LogViewerManager logViewerManager;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LongShort_AM_TabComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		afterCreateWidgets();
	}

	/**
	 * 
	 */
	private void afterCreateWidgets() {
		ILoggerManager emptyManager = new MemoryLoggerManager("Empty", true);
		try {
			logViewerManager = new LogViewerManager(this, SWT.None,
					STRATEGY_LOGGER_LONG_SHORT_ID, emptyManager);
		} catch (CoreException e) {
			//Adding a comment to avoid empty block warning.
		}
	}

	/**
	 * @return the logViewerManager
	 */
	public LogViewerManager getLogViewerManager() {
		return logViewerManager;
	}

}
