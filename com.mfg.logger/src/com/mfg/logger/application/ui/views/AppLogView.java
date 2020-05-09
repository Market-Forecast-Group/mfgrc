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

package com.mfg.logger.application.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;

import com.mfg.logger.ILoggerManager;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.ui.AppLogTableModel;
import com.mfg.logger.ui.ILogTableModel;
import com.mfg.logger.ui.views.AbstractLogView;
import com.mfg.logger.ui.views.AbstractLoggerViewControl;
import com.mfg.utils.ui.actions.CopyStructuredSelectionAction;

/**
 * @author arian
 * 
 */
public class AppLogView extends AbstractLogView {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ui.views.AbstractLogView#createLogModel()
	 */
	@Override
	protected ILogTableModel createLogModel() {
		return new AppLogTableModel(getLogManager().getReader());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.logger.ui.views.AbstractLogView#getLogManager()
	 */
	@Override
	public ILoggerManager getLogManager() {
		return LoggerPlugin.getDefault().getAppLogManager();
	}

	@Override
	public String getName() {
		return "AppLog";
	}

	@Override
	public AbstractLoggerViewControl getControl() {
		return null;
	}

	@Override
	protected void fillMenuBar(IMenuManager menuManager) {
		// TODO: override the default implementation because the bug #245
	}

	@Override
	protected void fillToolBar(IToolBarManager toolBar) {
		super.fillToolBar(toolBar);
		toolBar.add(new CopyStructuredSelectionAction());
	}
}
