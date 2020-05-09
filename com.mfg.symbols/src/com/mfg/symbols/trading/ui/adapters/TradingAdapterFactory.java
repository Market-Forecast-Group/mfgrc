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
package com.mfg.symbols.trading.ui.adapters;

import java.util.UUID;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.ui.UIPlugin;
import com.mfg.ui.editors.IEditable;

/**
 * @author arian
 * 
 */
@SuppressWarnings("rawtypes")
public class TradingAdapterFactory implements IAdapterFactory {

	private static Class[] list = { IEditable.class, IChartContentAdapter.class };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IChartContentAdapter.class) {
			if (adaptableObject instanceof TradingConfiguration) {
				return new TradingChartAdapater(
						(TradingConfiguration) adaptableObject);
			}
		} else if (adapterType == IEditable.class) {
			if (adaptableObject instanceof TradingConfiguration) {
				final TradingConfiguration configuration = (TradingConfiguration) adaptableObject;
				UUID inputId = configuration.getInfo().getInputConfiguratioId();
				InputConfiguration input = SymbolsPlugin.getDefault()
						.getInputsStorage().findById(inputId);
				final UUID symbolUUID = input.getInfo().getSymbolId();
				final SymbolConfiguration<?, ?> symbol = SymbolsPlugin
						.getDefault().findSymbolConfiguration(symbolUUID);

				return new IEditable() {

					@Override
					public IEditorPart openEditor() throws PartInitException {
						UIPlugin.getDefault();
						IEditorPart editor = UIPlugin.openEditor(symbol);
						if (editor != null) {
							((FormEditor) editor).setActivePage(configuration
									.getUUID().toString());
						}
						return editor;
					}
				};
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		return list;
	}

}
