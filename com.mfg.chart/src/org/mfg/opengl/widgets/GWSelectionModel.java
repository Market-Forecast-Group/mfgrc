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

package org.mfg.opengl.widgets;

/**
 * @author arian
 *
 */
public class GWSelectionModel implements IGWSelectionModel {

	private boolean selected = false;


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWSelectionModel#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return selected;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWSelectionModel#setSelected(boolean)
	 */
	@Override
	public void setSelected(final boolean selected1) {
		this.selected = selected1;
	}

}
