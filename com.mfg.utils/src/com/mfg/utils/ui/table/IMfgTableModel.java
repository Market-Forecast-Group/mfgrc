package com.mfg.utils.ui.table;

public interface IMfgTableModel {

	int getRowCount();

	String[] getColumnNames();

	Object getContent(int row, int column);

	boolean isEnabled(int row, int column);

	/**
	 * gets the HightLight ID. Zero for no highlight.
	 * 
	 * @param row
	 *            the row
	 * @param column
	 *            the column
	 * @return the highlight ID.
	 */
	int getHighLight(int row, int column);

}
