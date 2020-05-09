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
public class GWColorModel implements IGWColorModel {

	private float[] fg = COLOR_GRAY;
	private float[] bg = COLOR_BLACK;


	public GWColorModel(final float[] fg1, final float[] bg1) {
		this.fg = fg1;
		this.bg = bg1;
	}


	public GWColorModel() {
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWColorModel#getBackground()
	 */
	@Override
	public float[] getBackground() {
		return bg;
	}


	/**
	 * @param background
	 *            the background to set
	 */
	public void setBackground(final float[] bg1) {
		this.bg = bg1;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWColorModel#getForeground()
	 */
	@Override
	public float[] getForeground() {
		return fg;
	}


	/**
	 * @param foreground
	 *            the foreground to set
	 */
	public void setForeground(final float[] fg1) {
		this.fg = fg1;
	}
}
