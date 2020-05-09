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

import static java.lang.System.out;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author arian
 * 
 */
public class TestCheckboxTable {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Table table = new Table(shell, SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column0 = new TableColumn(table, SWT.NONE);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		int minWidth = 0;
		for (int i = 0; i < 10; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { "item " + i, "" });
			if (i % 3 == 0 || i % 5 == 0) {
				final int j = i;
				final Button b = new Button(table, SWT.CHECK);
				b.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						out.println("click " + j + " " + b.getSelection());
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						//Adding a comment to avoid empty block warning.
					}
				});
				b.setBackground(table.getBackground());
				b.pack();
				TableEditor editor = new TableEditor(table);
				Point size = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				editor.minimumWidth = size.x;
				minWidth = Math.max(size.x, minWidth);
				editor.minimumHeight = size.y;
				editor.horizontalAlignment = SWT.RIGHT;
				editor.verticalAlignment = SWT.CENTER;
				if (i % 3 == 0) {
					editor.setEditor(b, item, 1);
				} else {
					editor.setEditor(b, item, 2);
				}
			}
		}
		column0.pack();
		column1.pack();
		column1.setWidth(column1.getWidth() + minWidth);
		shell.setSize(300, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
