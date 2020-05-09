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
package com.mfg.symbols.ui.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.persist.interfaces.DoesNotExistObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.editors.TradingEditorPage;
import com.mfg.symbols.ui.ConfigurationSetsManager;
import com.mfg.ui.editors.StorageObjectEditor;
import com.mfg.utils.ImageUtils;

/**
 * @author arian
 * 
 */
public abstract class AbstractSymbolEditor extends StorageObjectEditor {
	private final WorkspaceStorageAdapter storageListener;
	final Map<Object, Object> pageMap;

	public AbstractSymbolEditor() {
		pageMap = new HashMap<>();

		storageListener = new WorkspaceStorageAdapter() {
			@SuppressWarnings("synthetic-access")
			// Variable pages haven't a getter in parent class
			@Override
			public void objectRemoved(IWorkspaceStorage storage, Object obj) {
				Object page = pageMap.remove(obj);
				if (page != null) {
					removePage(pages.indexOf(page));
				}
			}

			@Override
			public void objectAdded(IWorkspaceStorage sotarage, Object obj) {
				getEditorSite().getShell().getDisplay()
						.asyncExec(new Runnable() {

							@Override
							public void run() {
								try {
									updateEditorPages();
								} catch (PartInitException e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						});
			}
		};
		SymbolsPlugin.getDefault().getInputsStorage()
				.addStorageListener(storageListener);
		SymbolsPlugin.getDefault().getTradingStorage()
				.addStorageListener(storageListener);
	}

	@Override
	protected void createPages() {
		Object obj = getEditorInput().getStorageObject();
		if (obj instanceof DoesNotExistObject) {
			// close the editor
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							getEditorSite().getPage().closeEditor(
									AbstractSymbolEditor.this, false);
						}
					});
				}
			}.start();
			try {
				addPage(new FormPage(this, "does-not-exist", "Does not exist"));
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		} else {
			super.createPages();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getPageText(int)
	 */
	@Override
	public String getPageText(int pageIndex) {
		return super.getPageText(pageIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setPageText(int,
	 * java.lang.String)
	 */
	@Override
	public void setPageText(int pageIndex, String text) {
		super.setPageText(pageIndex, text);
	}

	/**
	 * @return
	 */
	protected String getMainPageId() {
		return getEditorInput().getStorageObject().getUUID().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.ui.editors.StorageObjectEditor#dispose()
	 */
	@Override
	public void dispose() {
		SymbolsPlugin.getDefault().getInputsStorage()
				.removeStorageListener(storageListener);
		SymbolsPlugin.getDefault().getTradingStorage()
				.removeStorageListener(storageListener);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {
			updateEditorPages();
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		int page = getEditorInput().getLastTab();
		if (page >= 0 && page < getPageCount()) {
			setActivePage(page);
		}
	}

	@Override
	public IFormPage setActivePage(String pageId) {
		IFormPage page = super.setActivePage(pageId);
		if (page == null) {
			// maybe we need to update the pages first, because maybe was added
			// an input or a trading
			try {
				updateEditorPages();
				super.setActivePage(pageId);
			} catch (PartInitException e) {
				e.printStackTrace();
			}

		}
		return page;
	}

	public void updateEditorPages() throws PartInitException {
		UUID uuid = ((BaseConfiguration<?>) getEditorInput().getStorageObject())
				.getUUID();
		SymbolsPlugin plugin = SymbolsPlugin.getDefault();

		List<InputConfiguration> inputs = plugin.getInputsStorage()
				.findBySymbolId(uuid);

		for (final InputConfiguration inputConfig : inputs) {
			// add input tabs
			FormPage inputPage = (FormPage) pageMap.get(inputConfig);
			if (inputPage == null) {
				inputPage = createInputPage(inputConfig);

				int pos = -1;
				// find where to add it
				String newName = inputConfig.getName();
				for (int i = 0; i < pages.size(); i++) {
					FormPage page = (FormPage) pages.get(i);
					if (page != null) {
						String pageName = page.getPartName();
						if (pageName.startsWith("Input")
								&& newName.compareTo(pageName) < 0) {
							pos = i;
							break;
						}
					}
				}
				if (pos == -1) {
					pos = addPage(inputPage);
				} else {
					addPage(pos, inputPage);
				}

				setPageImage(pos, ImageUtils.getBundledImage(plugin,
						SymbolsPlugin.INPUT_IMAGE_PATH));
				pageMap.put(inputConfig, inputPage);
			}

			// add trading tabs
			List<TradingConfiguration> tradings = plugin.getTradingStorage()
					.findByInput(inputConfig);
			for (TradingConfiguration tradingConfig : tradings) {
				if (!pageMap.containsKey(tradingConfig)) {
					TradingEditorPage tradingPage = new TradingEditorPage(this,
							tradingConfig.getUUID().toString(),
							tradingConfig.getName());
					tradingPage.setConfiguration(tradingConfig);

					String newName = tradingConfig.getName();
					int i = pages.indexOf(inputPage);
					int pos;
					for (pos = i + 1; pos < pages.size(); pos++) {
						FormPage page = (FormPage) pages.get(pos);
						if (page != null) {
							String pageName = page.getPartName();
							if (pageName.startsWith("Trading")) {
								if (newName.compareTo(pageName) < 0) {
									break;
								}
							} else {
								break;
							}
						}
					}
					pos = Math.min(pos, pages.size() - 1);
					addPage(pos, tradingPage);

					pageMap.put(tradingConfig, tradingPage);
					plugin.getSetsManager();
					Image img = ConfigurationSetsManager.getImage(tradingConfig
							.getInfo().getConfigurationSet());
					assert !img.isDisposed();
					setPageImage(pos, img);
				}
			}
		}
	}

	/**
	 * @param inputConfig
	 * @return
	 */
	protected abstract FormPage createInputPage(
			final InputConfiguration inputConfig);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setPageImage(int,
	 * org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setPageImage(int pageIndex, Image image) {
		super.setPageImage(pageIndex, image);
	}

	/**
	 * Create the Start/Stop commands for the given input. If the input is
	 * <code>null</code>, it means all the inputs will be started together with
	 * the symbol.
	 * 
	 * @param parent
	 * @param inputConfiguration
	 */
	public Object createCommandsSection(
			Composite parent,
			InputConfiguration inputConfiguration,
			@SuppressWarnings("unused") TradingConfiguration tradingConfiguration) {
		Label label = new Label(parent, SWT.NONE);
		String name = getEditorInput().getStorageObject().getName();
		label.setText("Commands to start symbol "
				+ name
				+ " and input "
				+ (inputConfiguration == null ? "ALL" : inputConfiguration
						.getName()));
		return null;
	}

	/**
	 * @return
	 */
	public SymbolConfiguration<?, ?> getSymbolConfiguration() {
		return (SymbolConfiguration<?, ?>) getEditorInput().getStorageObject();
	}

	/**
	 * Add extra actions for pages.
	 * 
	 * @param manager
	 */
	public void addExtraActions(IToolBarManager manager) {
		//
	}

}
