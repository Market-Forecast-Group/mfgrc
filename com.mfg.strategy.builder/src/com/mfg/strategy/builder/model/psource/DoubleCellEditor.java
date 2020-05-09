/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.model.psource;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class DoubleCellEditor extends TextCellEditor {

	ICellEditorValidator validator = new ICellEditorValidator() {
		@SuppressWarnings("unused")
		// TODO: Try just adding some value to a after assign the new Double object.
		@Override
		public String isValid(Object aValue) {
			try {
				Double a = new Double(aValue.toString());
			} catch (Exception e) {
				return e.getMessage();
			}
			return null;
		}
	};


	public DoubleCellEditor() {
		super();
		setValidator(validator);
	}


	public DoubleCellEditor(Composite aParent, int aStyle) {
		super(aParent, aStyle);
		setValidator(validator);
	}


	public DoubleCellEditor(Composite aParent) {
		super(aParent);
		setValidator(validator);
	}


	@Override
	protected void doSetValue(Object aValue) {
		super.doSetValue(aValue.toString());
	}


	@Override
	protected Object doGetValue() {
		return new Double(super.doGetValue().toString());
	}

}
