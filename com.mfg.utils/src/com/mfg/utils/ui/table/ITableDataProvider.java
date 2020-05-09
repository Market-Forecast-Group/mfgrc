package com.mfg.utils.ui.table;


public interface ITableDataProvider<T> {
	public T getRecord(int index);

	public int getRecordCount();

	public void addDataChangeListener(Runnable r);

	public void removeDataChangeListener(Runnable l);

	public void fireDataChanged();
}
