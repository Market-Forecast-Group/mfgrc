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
package com.mfg.logger.ui;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.memory.MemoryLogReader;

/**
 * @author arian
 * 
 */
public class LogContentProvider3 implements IStructuredContentProvider {
	private ILogReader _reader;
	private int _startIndex;
	private Viewer viewer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// DO NOTHING
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		if (newInput instanceof ILogReader) {
			_reader = (ILogReader) newInput;
			this.viewer = aViewer;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (_reader == null) {
			return new Object[0];
		}

		List<ILogRecord> list;
		int count = _reader.getRecordCount();
		if (_reader instanceof MemoryLogReader) {
			list = ((MemoryLogReader) _reader).getMemory();
			if (_startIndex > 0 && count > 0) {
				list = list.subList(_startIndex, count);
			}
		} else {
			if (_startIndex > 0 && count > 0) {
				list = _reader.read(_startIndex, count);
			} else {
				list = _reader.read();
			}
		}
		Object[] arr = list.toArray();
		for (int i = 0; i < arr.length / 2; i++) {
			int j = arr.length - 1 - i;
			Object arr_i = arr[i];
			arr[i] = arr[j];
			arr[j] = arr_i;
		}
		return arr;
	}

	/**
	 * 
	 */
	public void clear() {
		_startIndex = _reader.getRecordCount();
		this.viewer.setInput(_reader);
	}

}
