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
package com.mfg.logger;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wb.swt.FieldLayoutPreferencePage;

/**
 * @author arian
 * 
 */
public class TestWBPrefs extends FieldLayoutPreferencePage {

	/**
	 * Create the preference page.
	 */
	public TestWBPrefs() {
	}

	/**
	 * Create contents of the preference page.
	 * 
	 * @param parent
	 */
	@Override
	public Control createPageContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl_container = new GridLayout();
		gl_container.numColumns = 2;
		container.setLayout(gl_container);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("New Label");

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		{
			ComboFieldEditor comboFieldEditor = new ComboFieldEditor("id", " ",
					new String[][] { { "name_1", "value_1" },
							{ "name_2", "value_2" } }, composite);
			comboFieldEditor.getLabelControl(composite).setText("");
			addField(comboFieldEditor);
		}

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setText("New Label");

		Composite composite_1 = new Composite(container, SWT.NONE);
		{
			ComboFieldEditor comboFieldEditor = new ComboFieldEditor("id",
					"New ComboFieldEditor", new String[][] {
							{ "name_1", "value_1" }, { "name_2", "value_2" } },
					composite_1);
			comboFieldEditor.getLabelControl(composite_1).setText("");
			addField(comboFieldEditor);
		}
		return container;
	}

	/**
	 * Initialize the preference page.
	 * 
	 * @param workbench
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}

}
