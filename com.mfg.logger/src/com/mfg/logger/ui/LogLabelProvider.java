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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.mfg.logger.ILogRecord;

public class LogLabelProvider extends LabelProvider implements
		ITableLabelProvider, ILogConstants {

	private final ILogTableModel model;

	public LogLabelProvider(ILogTableModel aModel) {
		this.model = aModel;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// return
		// PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Object[] row = model.recordToArray((ILogRecord) element);
		Object cell = row[columnIndex];
		return cell == null ? "" : cell.toString();
	}
}
