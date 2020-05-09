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

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class LogContentProvider implements ILazyContentProvider {

	private ILogTableModel model;
	private TableViewer viewer;
	private int itemCount;

	public LogContentProvider() {
		itemCount = 100;
	}

	@Override
	public void updateElement(int index) {
		int realSize = model.getRecordCount();
		if (index < realSize) {
			if (index > itemCount / 3 && itemCount < realSize) {
				itemCount *= 3;
				itemCount = Math.min(itemCount, realSize);
				viewer.setItemCount(itemCount);
			}
			viewer.replace(model.getRecord(index), index);
		} else {
			// TODO: maybe put here a black item to the table.
		}
	}

	@Override
	public void dispose() {
		// DO NOTHING
	}

	@Override
	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) aViewer;
		this.model = (ILogTableModel) newInput;
		if (this.model != null) {
			int min = Math.min(model.getRecordCount(), itemCount);
			itemCount = Math.max(5, min);
			this.viewer.setItemCount(itemCount);
			this.viewer.getTable().setTopIndex(0);
		}
	}

}
