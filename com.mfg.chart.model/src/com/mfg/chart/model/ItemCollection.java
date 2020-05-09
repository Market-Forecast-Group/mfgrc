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
/**
 * 
 */

package com.mfg.chart.model;


/**
 * @author arian
 * 
 */
public class ItemCollection<T> implements IItemCollection {
	protected final T[] _data;
	private final int _start;
	private final int _len;


	public ItemCollection(T[] data1, int start1, int len1) {
		int startTmp, lenTmp;
		startTmp = start1 - 1;
		lenTmp = len1 + 2;
		startTmp = startTmp < 0 ? 0 : startTmp;
		lenTmp = startTmp + lenTmp > data1.length ? data1.length - startTmp : lenTmp;

		this._data = data1;
		this._start = startTmp;
		this._len = lenTmp < 0 ? 0 : Math.min(lenTmp, data1.length);
	}


	public ItemCollection(T[] data1) {
		this(data1, 0, data1.length);
	}


	public T getItem(int index) {
		return this._data[this._start + index];
	}


	@Override
	public int getSize() {
		return this._len;
	}

}
