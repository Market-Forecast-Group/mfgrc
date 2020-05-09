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

import javax.media.opengl.GL2;

import org.mfg.opengl.BitmapData;

/**
 * @author arian
 * 
 */
public class GWBitmap extends GWidget {

	private int bmpWidth;
	private int bmpHeight;
	private byte[] bmp;
	private int bmpY;
	private int bmpX;
	private String tooltip;

	/**
	 * @param parent
	 */
	public GWBitmap(final GWidget parent, final byte[] bmp1, final int bmpWidth1,
			final int bmpHeight1) {
		super(parent);
		this.bmp = bmp1;
		this.bmpWidth = bmpWidth1;
		this.bmpHeight = bmpHeight1;
		bmpX = 0;
		bmpY = 0;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip1
	 *            the tooltip to set
	 */
	public void setTooltip(String tooltip1) {
		this.tooltip = tooltip1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#getTooltip(int, int)
	 */
	@Override
	public String getTooltip(int x, int y) {
		return getTooltip();
	}

	/**
	 * @param layerPreferenceWidget
	 * @param bitmapRedo
	 */
	public GWBitmap(final GWidget parent, final BitmapData bmpData) {
		this(parent, bmpData.bitmap, bmpData.width, bmpData.height);
	}

	/**
	 * @return the bmpX
	 */
	public int getBmpX() {
		return bmpX;
	}

	/**
	 * @param bmpX1
	 *            the bmpX to set
	 */
	public void setBmpX(final int bmpX1) {
		this.bmpX = bmpX1;
	}

	/**
	 * @return the bmpY
	 */
	public int getBmpY() {
		return bmpY;
	}

	/**
	 * @param bmpY1
	 *            the bmpY to set
	 */
	public void setBmpY(final int bmpY1) {
		this.bmpY = bmpY1;
	}

	public void setBmpLocation(final int x, final int y) {
		setBmpX(x);
		setBmpY(y);
	}

	/**
	 * @return the shape
	 */
	public byte[] getBmp() {
		return bmp;
	}

	/**
	 * @param bmp1
	 *            the bmp to set
	 */
	public void setBmp(final byte[] bmp1, final int bmpWidth1, final int bmpHeight1) {
		this.bmp = bmp1;
		this.bmpWidth = bmpWidth1;
		this.bmpHeight = bmpHeight1;

	}

	/**
	 * @return the shapeHeight
	 */
	public int getBmpHeight() {
		return bmpHeight;
	}

	/**
	 * @return the shapeWidth
	 */
	public int getBmpWidth() {
		return bmpWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#paintWidget(javax.media.opengl.GL)
	 */
	@Override
	protected void paintWidget(final GL2 gl) {
		super.paintWidget(gl);

		final int x = getX() + getBmpX() + (getWidth() - getBestWidth()) / 2;
		final int y = getY() + getBmpY() + (getHeight() - getBestHeight()) / 2;

		gl.glColor4fv(getForegroundToPaint(), 0);
		gl.glRasterPos2i(x, y);
		gl.glBitmap(getBmpWidth(), getBmpHeight(), 0, 0, 0, 0, getBmp(), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#doLayout()
	 */
	@Override
	public void doLayout() {
		setBestHeight(Integer.valueOf(getBmpHeight()));
		setBestWidth(Integer.valueOf(getBmpWidth()));
		super.doLayout();
	}
}
