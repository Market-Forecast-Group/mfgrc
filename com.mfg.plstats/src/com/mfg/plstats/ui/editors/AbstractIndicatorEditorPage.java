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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author arian
 * 
 */
public abstract class AbstractIndicatorEditorPage extends FormPage {
	public AbstractIndicatorEditorPage(FormEditor editor, String id,
			String title) {
		super(editor, id, title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#getEditor()
	 */
	@Override
	public IndicatorEditor getEditor() {
		return (IndicatorEditor) super.getEditor();
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

	@Override
	protected final void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		FormToolkit toolkit = managedForm.getToolkit();
		toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(
				HyperlinkSettings.UNDERLINE_HOVER);
		toolkit.decorateFormHeading(form.getForm());

		addActions(managedForm);
		managedForm.getForm().getBody().setLayout(new FillLayout());

		// getEditor().setIgnoreSetDirty(true);
		userCreateFormContent(managedForm);
		// getEditor().setIgnoreSetDirty(false);
	}

	/**
	 * @param managedForm
	 */
	protected abstract void userCreateFormContent(IManagedForm managedForm);

	protected void addActions(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		IToolBarManager manager = form.getToolBarManager();

		IndicatorEditor editor = getEditor();

		manager.add(editor.getIndicatorActions().getCreateIndicatorAction());
		manager.add(editor.getIndicatorActions().getCreateProbabilitiesAction());
		manager.add(editor.getIndicatorActions().getExportIndicatorAction());
		manager.add(editor.getIndicatorActions().getOpenIndicatorChartAction());
		manager.add(new Separator());
		manager.add(editor.getIndicatorActions().getLoadIndicatorAction());
		manager.add(editor.getIndicatorActions().getIndexAction());

		manager.update(true);
		form.getForm().setToolBarVerticalAlignment(SWT.RIGHT);
	}

	public abstract DataBindingContext getDataBindingContext();
}
