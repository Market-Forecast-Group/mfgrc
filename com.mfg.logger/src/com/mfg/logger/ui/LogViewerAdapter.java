/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */

package com.mfg.logger.ui;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.mfg.logger.ILogRecord;

class LogTableModelAdapter implements ILogTableModel {

	private final ILogTableModel model;
	private int discardedRecordCount;

	public LogTableModelAdapter(ILogTableModel aModel) {
		super();
		this.model = aModel;
	}

	public ILogTableModel getModel() {
		return model;
	}

	@Override
	public synchronized String[] getColumnNames() {
		return model.getColumnNames();
	}

	@Override
	public synchronized Object[] recordToArray(ILogRecord record) {
		return model.recordToArray(record);
	}

	@Override
	public synchronized ILogRecord getRecord(int index) {
		int indexTmp = index;
		checkStartIndex();
		indexTmp = discardedRecordCount + indexTmp;
		indexTmp = Math.min(indexTmp, model.getRecordCount() - 1);
		return model.getRecord(indexTmp);
	}

	@Override
	public synchronized int getRecordCount() {
		checkStartIndex();
		return model.getRecordCount() - discardedRecordCount;
	}

	public synchronized void clear() {
		discardedRecordCount = model.getRecordCount();
	}

	private void checkStartIndex() {
		if (discardedRecordCount > model.getRecordCount()) {
			discardedRecordCount = 0;
		}
	}
}

public class LogViewerAdapter {

	protected final TableViewer viewer;
	private final LogTableModelAdapter modelAdapter;
	private boolean linkedToLastMessage;

	public LogViewerAdapter(Composite parent, ILogTableModel model,
			IContentProvider contentProvider) {
		this.modelAdapter = new LogTableModelAdapter(model);
		linkedToLastMessage = true;

		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.FULL_SELECTION
				| SWT.SELECTED);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new LogLabelProvider(modelAdapter));
		viewer.setUseHashlookup(true);

		Table table = viewer.getTable();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (String name : modelAdapter.getColumnNames()) {
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText(name);
			col.setWidth(100);
		}

		viewer.setInput(modelAdapter);
	}

	public void refresh() {
		viewer.refresh();
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public ILogTableModel getModel() {
		return modelAdapter;
	}

	public void scrollToEnd() {
		if (isLinkedToLastMessage()) {
			Table table = viewer.getTable();
			int i = modelAdapter.getRecordCount() + 1
					- table.getClientArea().height / table.getItemHeight();
			table.setTopIndex(i);
		}
	}

	public void scrollToIndex(int index) {
		Table table = viewer.getTable();
		table.setTopIndex(index);
	}

	public void clearTable() {
		modelAdapter.clear();
		viewer.getTable().clearAll();
		scrollToEnd();
	}

	public void setLinkedToLastMessage(boolean linked) {
		linkedToLastMessage = linked;
	}

	public boolean isLinkedToLastMessage() {
		return linkedToLastMessage;
	}
}
