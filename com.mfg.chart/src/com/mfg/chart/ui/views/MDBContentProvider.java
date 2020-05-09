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

import java.io.IOException;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.mfg.mdb.runtime.IRandomCursor;
import org.mfg.mdb.runtime.IRecord;
import org.mfg.mdb.runtime.MDB;

/**
 * @author arian
 * 
 */
public class MDBContentProvider implements ILazyContentProvider {
	private MDB<?> mdb;
	private TableViewer viewer;

	public static class Row {
		public Object[] data;
		public int index;
	}

	public MDBContentProvider() {
		this(200);
	}

	/**
	 * @param pageSize
	 */
	public MDBContentProvider(int pageSize) {
		super();
	}

	@Override
	public void dispose() {
		// Adding a comment to avoid empty block warning.
	}

	@Override
	public void inputChanged(Viewer viewer1, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer1;
		if (newInput != null && newInput instanceof MDB) {
			this.mdb = (MDB<?>) newInput;
			try {
				this.viewer.getTable().setItemCount((int) mdb.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			this.viewer.getTable().setItemCount(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILazyContentProvider#updateElement(int)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updateElement(int index) {
		try {
			@SuppressWarnings("resource")
			IRandomCursor c = mdb.thread_randomCursor();
			IRecord record = mdb.record(c, index);
			Row row = new Row();
			row.index = index;
			row.data = record.toArray();
			viewer.replace(row, index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
