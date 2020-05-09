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
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.interfaces.trading.Configuration;
import com.mfg.widget.interfaces.IProbabilitiesSettingsContainer;
import com.mfg.widget.ui.ProbabilitiesSettingsComposite;

/**
 * @author arian
 * 
 */
public class ProbabilitiesSettingsPage extends AbstractIndicatorEditorPage implements IProbabilitiesSettingsContainer {

	private ProbabilitiesSettingsComposite comp;


	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public ProbabilitiesSettingsPage(FormEditor editor) {
		super(editor, "probabilitiesSettings", "Probabilities Settings");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.explorer.ui.editors.AbstractIndicatorEditorPage#userCreateFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void userCreateFormContent(IManagedForm managedForm) {
		comp = new ProbabilitiesSettingsComposite(managedForm.getForm().getBody(), SWT.NONE, this);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.explorer.ui.editors.AbstractIndicatorEditorPage#getDataBindingContext()
	 */
	@Override
	public DataBindingContext getDataBindingContext() {
		return comp.getDataBindingContext();
	}


	@Override
	public Configuration getConfiguration() {
		return getEditorInput().getProbabilitiesSettings();
	}
}
