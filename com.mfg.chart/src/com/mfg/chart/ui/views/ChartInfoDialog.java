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
package com.mfg.chart.ui.views;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.mfg.chart.backend.opengl.Chart;

/**
 * @author arian
 * 
 */
public class ChartInfoDialog extends Dialog {
	private Table _table;
	private TableViewer _tableViewer;
	private Chart _chart;
	private DataLayersCanvas _dataLayersCanvas;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ChartInfoDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE);
		setBlockOnOpen(false);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		_tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		_table = _tableViewer.getTable();
		_table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 300;
		_table.setLayoutData(gd_table);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {
				Map.Entry<String, Object> entry = (Entry<String, Object>) element;
				return entry.getKey();
			}
		});
		TableColumn tblclmnName = tableViewerColumn.getColumn();
		tblclmnName.setWidth(207);
		tblclmnName.setText("Property");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				_tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {
				Map.Entry<String, Object> entry = (Entry<String, Object>) element;
				return entry.getValue() == null ? "" : entry.getValue()
						.toString();
			}
		});
		TableColumn tblclmnValue = tableViewerColumn_1.getColumn();
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("Value");

		Label lblDataLayersTimeline = new Label(container, SWT.NONE);
		lblDataLayersTimeline.setText("Data Layers Timeline (Physical):");

		_dataLayersCanvas = new DataLayersCanvas(container);
		GridData gd_dataLayersCanvas = new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1);
		gd_dataLayersCanvas.heightHint = 200;
		_dataLayersCanvas.setLayoutData(gd_dataLayersCanvas);
		_tableViewer.setContentProvider(new ArrayContentProvider());

		afterCreateWidgets();

		return container;
	}

	/**
	 * 
	 */
	void afterCreateWidgets() {
		Map<String, Object> info = new LinkedHashMap<>();
		_chart.putInfo(info);
		_tableViewer.setInput(info.entrySet().toArray());
		getShell().setText("Chart Info");

		_dataLayersCanvas.setChart(_chart);
	}

	public void setChart(Chart chart) {
		this._chart = chart;
	}

	/**
	 * @return the chart
	 */
	public Chart getChart() {
		return _chart;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnRefresh = createButton(parent, IDialogConstants.RETRY_ID,
				IDialogConstants.RETRY_LABEL, true);
		btnRefresh.setText("Refresh");
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				afterCreateWidgets();
			}
		});
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(646, 521);
	}
}
