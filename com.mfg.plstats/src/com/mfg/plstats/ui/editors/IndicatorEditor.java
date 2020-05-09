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

package com.mfg.plstats.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.ui.actions.IIndicatorActions;

/**
 * @author arian
 * 
 */
public class IndicatorEditor extends FormEditor {

	public static String ID = "com.mfg.plstats.ui.editors.IndicatorEditor";

	private IIndicatorActions indicatorActions;

	private WorkspaceStorageAdapter storageListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		setPartName(getEditorInput().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public IndicatorEditorInput getEditorInput() {
		return (IndicatorEditorInput) super.getEditorInput();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {

		initListeners();

		initActions();

		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(this.getContainer(), "com.mfg.help.indicatorContext");

		try {
			addPage(new IndicatorOverviewPage(this));
			addPage(new IndicatorSettingsPage(this));
			addPage(new ProbabilitiesSettingsPage(this));
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	private void initListeners() {
		storageListener = new WorkspaceStorageAdapter() {
			@Override
			public void objectRemoved(IWorkspaceStorage storage, Object obj) {
				IndicatorEditorInput input = getEditorInput();
				if (input.getConfiguration() == obj) {
					close(false);
				}
			}
		};
		PLStatsPlugin.getDefault().getIndicatorStorage()
				.addStorageListener(storageListener);
	}

	/**
	 * 
	 */
	private void initActions() {
		final IIndicatorConfiguration config = getEditorInput()
				.getConfiguration();
		indicatorActions = PLStatsPlugin.getDefault().getIndicatorManager()
				.createIndicatorActions(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	@Override
	public void dispose() {

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (activePage != null && activePage.getEditorReferences().length == 0) {
			activePage.setEditorAreaVisible(false);
		}

		super.dispose();
		indicatorActions.dispose();
		PLStatsPlugin.getDefault().getIndicatorStorage()
				.removeStorageListener(storageListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#close(boolean)
	 */
	@Override
	public void close(boolean save) {
		super.close(save);

		PLStatsPlugin.getDefault().getIndicatorStorage()
				.removeStorageListener(storageListener);
	}

	/**
	 * @return the indicatorActions
	 */
	public IIndicatorActions getIndicatorActions() {
		return indicatorActions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// Adding some comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// Adding some comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}
