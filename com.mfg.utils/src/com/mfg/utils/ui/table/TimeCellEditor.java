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
package com.mfg.utils.ui.table;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;

/**
 * @author arian
 * 
 */
public class TimeCellEditor extends CellEditor {

	private DateTime dateTime;

	public TimeCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected Control createControl(Composite parent) {
		dateTime = new DateTime(parent, SWT.TIME | SWT.MEDIUM);
		return dateTime;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Object doGetValue() {
		return new Date(dateTime.getYear(), dateTime.getMonth(),
				dateTime.getDay(), dateTime.getHours(), dateTime.getMinutes(),
				dateTime.getSeconds());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
	 */
	@Override
	protected void doSetFocus() {
		dateTime.forceFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 */
	@SuppressWarnings({ "deprecation", "boxing" })
	@Override
	protected void doSetValue(Object value) {
		Date d = (Date) value;
		dateTime.setYear(d.getYear());
		dateTime.setMonth(d.getMonth());
		dateTime.setData(d.getDay());
		dateTime.setHours(d.getHours());
		dateTime.setMinutes(d.getMinutes());
		dateTime.setSeconds(d.getSeconds());
	}

}
