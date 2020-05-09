package com.mfg.utils.ui.table;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;

public class TableModelMiddleMan {

	IMfgTableModel model;

	public IMfgTableModel getModel() {
		return model;
	}

	public void setModel(IMfgTableModel aModel) {
		model = aModel;
	}

	public int getColumnCount() {
		if (model == null)
			return 0;
		return model.getColumnNames().length;
	}

	public String getColumnName(int index) {
		return model.getColumnNames()[index];
	}

	public static IContentProvider getContentProvider() {
		return new ArrayContentProvider();
	}

	@SuppressWarnings("boxing")
	public Integer[] getIndexesInput() {
		int rowCount = model == null ? 0 : model.getRowCount();
		Integer[] res = new Integer[rowCount];
		for (int i = 0; i < res.length; i++) {
			res[i] = i;
		}
		return res;
	}

	public ColumnLabelProvider getColumn(final int index) {
		return new MFGColumnLabelProvider(index, this);
	}
}
