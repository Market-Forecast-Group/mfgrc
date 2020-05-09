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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author arian
 * 
 */
public class LazyContentProviderTest {

	protected Shell shell;
	private Table table;
	private TableViewer tableViewer;

	static class ContentProvider implements ILazyContentProvider {

		private TableViewer viewer;

		@Override
		public void dispose() {
			// Adding a comment to avoid empty block warning.
		}

		@SuppressWarnings("boxing")
		@Override
		public void inputChanged(Viewer aViewer, Object oldInput,
				Object newInput) {
			this.viewer = (TableViewer) aViewer;
			if (newInput != null) {
				this.viewer.getTable().setItemCount((Integer) newInput);
			}
		}

		@Override
		public void updateElement(int index) {
			viewer.replace("element " + index, index);
			out.println("update " + index);
		}

	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LazyContentProviderTest window = new LazyContentProviderTest();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((String) element).split(" ")[1];
			}
		});
		TableColumn tblclmnIndex = tableViewerColumn.getColumn();
		tblclmnIndex.setWidth(100);
		tblclmnIndex.setText("Index");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return "Elemento " + ((String) element).split(" ")[1];
			}
		});
		TableColumn tblclmnSpanish = tableViewerColumn_1.getColumn();
		tblclmnSpanish.setWidth(100);
		tblclmnSpanish.setText("Spanish");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		TableColumn tblclmnEnglish = tableViewerColumn_2.getColumn();
		tblclmnEnglish.setWidth(100);
		tblclmnEnglish.setText("English");
		tableViewer.setContentProvider(new ContentProvider());

		afterWidgetsCreate();
	}

	/**
	 * 
	 */
	@SuppressWarnings("boxing")
	private void afterWidgetsCreate() {
		tableViewer.setInput(1000);
	}

}
