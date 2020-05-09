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

import org.mfg.opengl.IGLDrawable;

/**
 * @author arian
 * 
 */
public class GWRoot extends GWidget implements IGLDrawable {

	private boolean firstTimePaint = true;

	/**
	 * @param parent
	 */
	public GWRoot() {
		super(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.glchart.IGLDrawable#init(javax.media.opengl.GL)
	 */
	@Override
	public void init(final GL2 gl) {
		//Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.glchart.IGLDrawable#display(javax.media.opengl.GL, int, int)
	 */
	@Override
	public void display(final GL2 gl, final int width, final int height) {
		if (firstTimePaint) {
			doLayout();
			firstTimePaint = false;
		}
		paintChildren(gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#doLayout()
	 */
	@Override
	public void doLayout() {
		super.doLayout();
		final GWBorder border = getBorder();
		setWidth(getBestWidth() + border.getLeft() + border.getRight());
		setHeight(getBestHeight() + border.getTop() + border.getBottom());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#reshape(javax.media.opengl.GL2, int, int)
	 */
	@Override
	public void reshape(GL2 gl, int width, int height) {
		setBounds(0, 0, width, height);
		doLayout();
		reshaped(width, height);
	}
}
