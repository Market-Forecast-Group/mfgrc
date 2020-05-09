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
package com.mfg.ui.widgets;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author arian
 * 
 */
public class FractionCellEditor extends CellEditor {

	public static class Value {
		private int numerator;
		private int denominator;

		public Value(int aNumerator, int aDdenominator) {
			this.numerator = aNumerator;
			this.denominator = aDdenominator;
		}

		public int getNumerator() {
			return numerator;
		}

		public void setNumerator(int aNumerator) {
			this.numerator = aNumerator;
		}

		public int getDenominator() {
			return denominator;
		}

		public void setDenominator(int aDenominator) {
			this.denominator = aDenominator;
		}

	}

	private FractionField control;

	public FractionCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected Control createControl(Composite parent) {
		control = new FractionField(parent, SWT.NONE);
		KeyListener listener = new KeyAdapter() {
			@SuppressWarnings("synthetic-access")
			// Inherit method focusLost.
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.character == SWT.ESC) {
					focusLost();
				}
			}
		};
		control.getNumeratorText().addKeyListener(listener);
		control.getDenominatorText().addKeyListener(listener);
		return control;
	}

	@Override
	protected Object doGetValue() {
		Value value = new Value(control.getNumerator(),
				control.getDenominator());
		return value;
	}

	@Override
	protected void doSetFocus() {
		control.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
		Value v = (Value) value;
		control.setNumerator(v.getNumerator());
		control.setDenominator(v.getDenominator());
	}

}
