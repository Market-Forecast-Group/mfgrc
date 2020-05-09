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

import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.ui.IIndicatorSettingsContainer;
import com.mfg.widget.ui.IndicatorSettingsComposite;

/**
 * @author arian
 * 
 */
public class IndicatorSettingsPage extends AbstractIndicatorEditorPage
		implements IIndicatorSettingsContainer {

	private IndicatorSettingsComposite comp;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public IndicatorSettingsPage(FormEditor editor) {
		super(editor, "indicatorSettings", "Indicator Settings");
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
		comp = new IndicatorSettingsComposite(managedForm.getForm().getBody(),
				SWT.NONE);
		comp.setIndicatorSettings(getIndicatorSettings());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.ui.IIndicatorSettingsContainer#getIndicatorSettings()
	 */
	@Override
	public IndicatorParamBean getIndicatorSettings() {
		return getEditorInput().getIndicatorSettings();
	}
}
