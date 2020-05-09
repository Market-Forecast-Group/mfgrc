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
package com.mfg.symbols.ui.widgets;

import org.eclipse.swt.widgets.Text;

import com.mfg.ui.widgets.EmptyTextFieldPainter;

/**
 * @author arian
 * 
 */
public class EmptyTickFieldPainter extends EmptyTextFieldPainter {

	public static void addPainterToWidgets(Text... texts) {
		for (Text t : texts) {
			t.addPaintListener(new EmptyTickFieldPainter(t));
		}
	}

	/**
	 * @param text
	 * @param message
	 */
	public EmptyTickFieldPainter(Text text) {
		super(text, "(Automatically Computed)");
	}

}
