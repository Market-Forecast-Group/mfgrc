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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.plstats.ui.actions.IIndicatorActions;
import com.mfg.utils.ui.UIUtils;

/**
 * @author arian
 * 
 */
public class IndicatorOverviewPage extends AbstractIndicatorEditorPage {

	public static final String ID = "indicatorOverview";
	private IndicatorOverviewComposite comp;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public IndicatorOverviewPage(FormEditor editor) {
		super(editor, ID, "Overview");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.explorer.ui.editors.AbstractIndicatorEditorPage#userCreateFormContent
	 * (org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void userCreateFormContent(IManagedForm managedForm) {
		comp = new IndicatorOverviewComposite(managedForm.getForm().getBody(),
				getEditorInput().getConfiguration());
		IIndicatorActions indicatorActions = getEditor().getIndicatorActions();
		UIUtils.connectLinkWithAction(comp.getCreateIndicatorLink(),
				indicatorActions.getCreateIndicatorAction());
		UIUtils.connectLinkWithAction(comp.getCreateProbabilitiesLink(),
				indicatorActions.getCreateProbabilitiesAction());
		UIUtils.connectLinkWithAction(comp.getExportIndicatorLink(),
				indicatorActions.getExportIndicatorAction());
		UIUtils.connectLinkWithAction(comp.getLoadIndicatorLink(),
				indicatorActions.getLoadIndicatorAction());
		UIUtils.connectLinkWithAction(comp.getCreateIndexLink(),
				indicatorActions.getIndexAction());
		UIUtils.connectLinkWithAction(comp.getOpenChartLink(),
				indicatorActions.getOpenIndicatorChartAction());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.explorer.ui.editors.AbstractIndicatorEditorPage#getDataBindingContext
	 * ()
	 */
	@Override
	public DataBindingContext getDataBindingContext() {
		return comp.getDataBindingContext();
	}

}
