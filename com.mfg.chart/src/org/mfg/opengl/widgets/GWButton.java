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
public class GWButton extends GWLabel implements IGWButton {

	private IGWSelectionModel selectionModel;


	public GWButton(final GWidget parent, final String text) {
		super(parent, text);
		setSelectionModel(new GWSelectionModel());
	}


	public GWButton(final GWidget parent) {
		this(parent, "");
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWButton#getSelectionModel()
	 */
	@Override
	public IGWSelectionModel getSelectionModel() {
		return selectionModel;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWButton#setSelectionModel(org.mfg.opengl.widgets.IGWSelectionModel)
	 */
	@Override
	public void setSelectionModel(final IGWSelectionModel model) {
		selectionModel = model;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWButton#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return selectionModel.isSelected();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.GWidget#getBackgroundToPaint()
	 */
	@Override
	public float[] getBackgroundToPaint() {
		return isSelected() ? getForeground() : getBackground();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.GWidget#getForegroundToPaint()
	 */
	@Override
	public float[] getForegroundToPaint() {
		return isSelected() ? getBackground() : getForeground();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.GWidget#mouseClicked(int, int)
	 */
	@Override
	public void mouseClicked(final int x, final int y) {
		getSelectionModel().setSelected(!isSelected());
	}

}
