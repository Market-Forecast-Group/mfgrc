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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;

public class EmptyTextFieldPainter implements PaintListener {
	private final Text text;
	final Font font;
	private final String message;

	public EmptyTextFieldPainter(Text aText, String aMessage) {
		this.text = aText;
		this.message = aMessage;
		FontData[] fontData = aText.getFont().getFontData();
		font = new Font(aText.getDisplay(), fontData[0].getName(),
				fontData[0].getHeight(), SWT.ITALIC);
		aText.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}
		});
	}

	@Override
	public void paintControl(PaintEvent e) {
		String str = text.getText();
		boolean readOnly = (text.getStyle() & SWT.READ_ONLY) != 0;
		boolean focus = text.isFocusControl();
		if ((readOnly || !focus) && (str == null || str.length() == 0)) {
			e.gc.setFont(font);
			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
			e.gc.drawText(message, 0, 0);
		}
	}

}