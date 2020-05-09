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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.mfg.logger.ILogRecord;
import com.mfg.logger.memory.MemoryLogReader;

/**
 * @author arian
 * 
 */
public class LogContentProvider2 implements IStructuredContentProvider {

	private ILogTableModel model;
	private TableViewer viewer;

	public LogContentProvider2() {
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
			this.viewer.getTable().setTopIndex(0);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		LogTableModelAdapter adapter = ((LogTableModelAdapter) model);
		List<ILogRecord> list = ((MemoryLogReader) ((AbstractLogTableModel) adapter
				.getModel()).getReader()).getMemory();
		return list.toArray();
	}

}
