/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.commands;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory;

import com.mfg.strategy.builder.StrategyBuilderPlugin;

public class ImportAction extends Action {

	public ImportAction() {
		init();
	}


	protected void init() {
		setText("Import");
		setId(ActionFactory.IMPORT.getId());
		setActionDefinitionId(ActionFactory.IMPORT.getId());
		setImageDescriptor(StrategyBuilderPlugin.getImageDescriptor("/icons/import.gif"));
		setEnabled(true);
	}


	@Override
	public void run() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
