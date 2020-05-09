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

package com.mfg.strategy;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.mfg.interfaces.trading.Configuration.SCMode;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.utils.ObjectListener;
import com.mfg.utils.Utils;

/**
 * @author arian
 * 
 */
public class ConformationQueueComposite extends Composite implements
		ObjectListener<IConfirmationRequest> {

	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private DataBindingContext m_bindingContext;
	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private final ConformationQueueComposite self = this;
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	final Composite composite_1;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ConformationQueueComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		composite_1 = new Composite(this, SWT.NONE);
		toolkit.adapt(composite_1);
		toolkit.paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(3, false));

		Label flabel_1 = new Label(composite_1, SWT.NONE);
		flabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.adapt(flabel_1, true, true);
		flabel_1.setText("Text");

		Label flabel_2 = new Label(composite_1, SWT.NONE);
		toolkit.adapt(flabel_2, true, true);
		flabel_2.setText("Confirm");

		Label flabel_6 = new Label(composite_1, SWT.NONE);
		flabel_6.setAlignment(SWT.CENTER);
		toolkit.adapt(flabel_6, true, true);
		flabel_6.setText("Cancel");

		final Composite composite_5 = toolkit
				.createCompositeSeparator(composite_1);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1);
		gd_composite_5.heightHint = 2;
		composite_5.setLayoutData(gd_composite_5);
		toolkit.paintBordersFor(composite_5);

	}

	@SuppressWarnings("unused")
	// Maybe used on inner classes.
	private static String[] getItems() {
		SCMode[] t = getSCValues();
		String[] res = new String[t.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = t[i].toString();
		}
		return res;
	}

	public static SCMode[] getSCValues() {
		return SCMode.values();
	}

	public static SCMode getSCValuesData() {
		return SCMode.NoFilter;
	}

	public Color getBlueColor() {
		return toolkit.getColors().getColor(IFormColors.TB_TOGGLE);
	}

	static List<IConfirmationRequest> getTheList() {
		return SymbolsPlugin.getDefault().getConfirmationsRequests();
	}

	@Override
	public void handle(final IConfirmationRequest aObject) {

		final Label flabel = new Label(composite_1, SWT.NONE);
		toolkit.adapt(flabel, true, true);
		flabel.setText(aObject.toString());

		final Button fbutton_1 = new Button(composite_1, SWT.NONE);
		toolkit.adapt(fbutton_1, true, true);
		fbutton_1.setText("Confirm");

		final Button fbutton = new Button(composite_1, SWT.NONE);
		toolkit.adapt(fbutton, true, true);
		fbutton.setText("Cancel");

		final Composite composite_5 = toolkit
				.createCompositeSeparator(composite_1);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1);
		gd_composite_5.heightHint = 2;
		composite_5.setLayoutData(gd_composite_5);
		toolkit.paintBordersFor(composite_5);

		fbutton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent aE) {
				aObject.confirm();
				flabel.dispose();
				fbutton.dispose();
				fbutton_1.dispose();
				composite_5.dispose();
				composite_1.layout();
			}
		});

		fbutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent aE) {
				aObject.cancel();
				flabel.dispose();
				fbutton.dispose();
				fbutton_1.dispose();
				composite_5.dispose();
				composite_1.layout();
			}
		});
		Utils.debug_var(12345, "confirmation added " + getTheList().size());
		composite_1.redraw();
		composite_1.layout();
	}

}
