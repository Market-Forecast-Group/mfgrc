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
package com.mfg.symbols.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.chart.ui.views.ChartView;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.ui.editors.StorageObjectEditorInput;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class WorkbenchSymbolsSelectionListener implements
		ISelectionChangedListener {

	private final boolean _openEditor;

	public WorkbenchSymbolsSelectionListener(boolean openEditor) {
		this._openEditor = openEditor;
	}

	public WorkbenchSymbolsSelectionListener() {
		this(true);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		StructuredSelection sel = (StructuredSelection) event.getSelection();
		final Object obj = sel.getFirstElement();
		// if it is an storage obj
		if (obj instanceof IStorageObject) {
			if (_openEditor) {
				showInEditors(obj);
			}
			if (obj instanceof TradingConfiguration) {
				showInTradingParts((TradingConfiguration) obj);
			} else {
				showInCharts(obj);
			}
		}

	}

	/**
	 * @param obj
	 */
	private static void showInTradingParts(TradingConfiguration trading) {
		// XXX: do not activate parts, it makes an odd effect on the action bar
		List<ITradingView> views = SymbolsPlugin.getDefault()
				.getOpenTradingViews();
		boolean set = false;
		for (ITradingView view : views) {
			if (view.getConfigurationSet() == trading.getInfo()
					.getConfigurationSet()) {
				// PartUtils.activatePart(view.getPart());
				if (view.getConfiguration() != trading) {
					view.setConfiguration(trading);
				}
				set = true;
			}
		}
		if (!set) {
			for (ITradingView view : views) {
				if (view.getConfiguration() == null) {
					view.setConfiguration(trading);
				}
			}
		}
	}

	/**
	 * @param obj
	 */
	private static void showInCharts(Object obj) {
		// XXX: do not activate parts, it makes an odd effect on the action bar

		List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
		boolean set = false;
		for (ChartView view : views) {
			if (view.getContent() == obj) {
				// view.getSite().getPage().activate(view);
				set = true;
			}
		}
		if (!set) {
			for (ChartView view : views) {
				if (view.getContent() == null) {
					view.setContent(obj);
					// view.getSite().getPage().activate(view);
				}
			}
		}
	}

	/**
	 * @param obj
	 */
	private static void showInEditors(final Object obj) {
		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] refs = page.getEditorReferences();
		// for each editor
		for (final IEditorReference ref : refs) {
			IEditorInput editorInput;
			try {
				editorInput = ref.getEditorInput();
				// if it is an storage object editor
				if (editorInput instanceof StorageObjectEditorInput) {
					boolean open = false;
					Object editorObj = ((StorageObjectEditorInput<?>) editorInput)
							.getStorageObject();
					if (editorObj == obj) {
						open = true;
					}
					if (editorObj instanceof SymbolConfiguration<?, ?>) {
						// if is an input
						SymbolsPlugin plugin = SymbolsPlugin.getDefault();
						InputConfiguration[] inputs = plugin.getInputsStorage()
								.findBySymbol(
										(SymbolConfiguration<?, ?>) editorObj);
						if (Arrays.asList(inputs).contains(obj)) {
							open = true;
						}
						// if is a trading
						if (obj instanceof TradingConfiguration) {
							List<TradingConfiguration> tradings = plugin
									.getTradingStorage().findByInput(inputs);
							if (tradings.contains(obj)) {
								open = true;
							}
						}
					}
					// if open, open it at the specific page (uuid)
					if (open) {
						final IEditorPart editor = ref.getEditor(false);
						if (editor != null) {
							page.activate(editor);
							((FormEditor) editor)
									.setActivePage(((IStorageObject) obj)
											.getUUID().toString());
						}
					}
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}
