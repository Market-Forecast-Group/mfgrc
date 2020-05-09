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

import org.mfg.opengl.BitmapData;

/**
 * @author arian
 * 
 */
public class GWBitmapButton extends GWBitmap implements IGWButton {

	private IGWSelectionModel selectionModel;
	private final byte[] onBmp;
	private final byte[] offBmp;

	/**
	 * @param parent
	 * @param bmp
	 * @param bmpWidth
	 * @param bmpHeight
	 */
	public GWBitmapButton(final GWidget parent, final byte[] bmp,
			final int bmpWidth, final int bmpHeight) {
		this(parent, bmp, bmp, bmpWidth, bmpHeight);
	}

	public GWBitmapButton(final GWidget parent, final BitmapData bmpData) {
		this(parent, bmpData.bitmap, bmpData.width, bmpData.height);
	}

	/**
	 * @param parent
	 * @param bmp
	 * @param bmpWidth
	 * @param bmpHeight
	 */
	public GWBitmapButton(final GWidget parent, final byte[] onBmp1,
			final byte[] offBmp1, final int bmpWidth, final int bmpHeight) {
		super(parent, onBmp1, bmpWidth, bmpHeight);
		selectionModel = new GWSelectionModel();
		this.onBmp = onBmp1;
		this.offBmp = offBmp1;
	}

	public GWBitmapButton(GWidget parent, BitmapData bmp,
			IGWSelectionModel selectionModel1) {
		this(parent, bmp);
		setSelectionModel(selectionModel1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWBitmap#getBmp()
	 */
	@Override
	public byte[] getBmp() {
		return isSelected() ? onBmp : offBmp;
	}

	/**
	 * @return the selected
	 */
	@Override
	public boolean isSelected() {
		return getSelectionModel().isSelected();
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
	 * @see org.mfg.opengl.widgets.IGWButton#getSelectionModel()
	 */
	@Override
	public IGWSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.widgets.IGWButton#setSelectionModel(org.mfg.opengl.widgets
	 * .IGWSelectionModel)
	 */
	@Override
	public void setSelectionModel(final IGWSelectionModel model) {
		selectionModel = model;
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
