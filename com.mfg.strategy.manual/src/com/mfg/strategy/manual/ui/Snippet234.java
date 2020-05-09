/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.mfg.strategy.manual.ui;

/* 
 * Table example snippet: Fixed first column and horizontal scroll remaining columns
 *
 * For more info on custom-drawing TableItem and TreeItem content see 
 * http://www.eclipse.org/articles/article.php?file=Article-CustomDrawingTableAndTreeItems/index.html
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.3
 */

import static org.eclipse.swt.SWT.H_SCROLL;
import static org.eclipse.swt.SWT.V_SCROLL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Snippet234 {
	public static void main(String[] args) {
		int rowCount = 40;
		int columnCount = 15;
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		Composite parent = new Composite(shell, SWT.BORDER);
		parent.setLayout(new FillLayout());
		final Table leftTable = new Table(parent, SWT.MULTI
				| SWT.FULL_SELECTION | H_SCROLL | V_SCROLL);
		leftTable.setHeaderVisible(true);
		// Create columns
		TableColumn column1 = new TableColumn(leftTable, SWT.NONE);
		column1.setText("Name");
		column1.setWidth(150);
		for (int i = 0; i < columnCount; i++) {
			// DO NOTHING
		}
		// Create rows
		for (int i = 0; i < rowCount; i++) {
			TableItem item = new TableItem(leftTable, SWT.NONE);
			item.setText("item " + i);
			for (int j = 0; j < columnCount; j++) {
				item.setText(j, "Item " + i + " value @ " + j);
			}
		}

		shell.setSize(600, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}