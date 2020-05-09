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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * @author arian
 *
 */
public class GWSeparator extends GWidget {
	private boolean vertical;
	private int extraLong;

	/**
	 * @param parent
	 */
	public GWSeparator(final GWidget parent, final boolean vertical1) {
		super(parent);
		this.vertical = vertical1;
		getBorder().setVisible(false);
		extraLong = 0;
	}

	/**
	 * @return the extraLong
	 */
	public int getExtraLong() {
		return extraLong;
	}

	/**
	 * @param extraLong1
	 *            the extraLong to set
	 */
	public void setExtraLong(final int extraLong1) {
		this.extraLong = extraLong1;
	}

	/**
	 * @return the vertical
	 */
	public boolean isVertical() {
		return vertical;
	}

	/**
	 * @param vertical1
	 *            the vertical to set
	 */
	public void setVertical(final boolean vertical1) {
		this.vertical = vertical1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.GWidget#paintWidget(javax.media.opengl.GL)
	 */
	@Override
	protected void paintWidget(final GL2 gl) {
		int x1, x2, y1, y2;

		if (isVertical()) {
			x1 = x2 = getX() + getWidth() / 2;
			y1 = getY() - getExtraLong();
			y2 = getY() + getHeight() + getExtraLong();
		} else {
			x1 = getX() - getExtraLong();
			x2 = getX() + getWidth() + getExtraLong();
			y1 = y2 = getY() + getHeight() / 2;
		}

		gl.glLineWidth(getBorder().getLineWidth());
		gl.glColor4fv(getForegroundToPaint(), 0);
		gl.glBegin(GL.GL_LINE_STRIP);

		gl.glVertex2i(x1, y1);
		gl.glVertex2i(x2, y2);

		gl.glEnd();
		gl.glLineWidth(1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.GWidget#doLayout()
	 */
	@Override
	public void doLayout() {
		if (isVertical()) {
			setBestHeight(Integer.valueOf(30));
			setBestWidth(Integer.valueOf(3));
		} else {
			setBestHeight(Integer.valueOf(3));
			setBestWidth(Integer.valueOf(30));
		}
		super.doLayout();
	}
}
