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
package com.mfg.symbols.ui.commands;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.dm.symbols.SymbolData2;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.ui.commands.AbstractNewStorageObjectHandler;

/**
 * @author arian
 * 
 */
public abstract class AbstractNewSymbolConfigurationHandler<T extends SymbolConfiguration<?, ?>>
		extends AbstractNewStorageObjectHandler<T> {

	protected abstract SimpleStorage<? extends SymbolData2> getSymbolsStorage();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.ui.commands.AbstractNewConfigurationHandler#execute(org.eclipse
	 * .core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<? extends SymbolData2> storageObjects = getSymbolsStorage()
				.getObjects();

		if (storageObjects.isEmpty()) {
			MessageDialog
					.openInformation(
							HandlerUtil.getActiveShell(event),
							"New " + getInitialObjectName()
									+ " Configuration",
							"To create a "
									+ getInitialObjectName()
									+ " symbol configuration, there should be at least one "
									+ getInitialObjectName()
									+ " symbol available.");
			return null;
		}

		@SuppressWarnings("unchecked")
		SymbolConfiguration<SymbolData2, SymbolConfigurationInfo<SymbolData2>> configuration = (SymbolConfiguration<SymbolData2, SymbolConfigurationInfo<SymbolData2>>) super
				.execute(event);

		configuration.getInfo().setSymbol(storageObjects.get(0));

		return configuration;
	}
}
