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

import org.mfg.opengl.IGLConstants;

/**
 * @author arian
 * 
 */
public class GWBorder implements IGLConstants {
	private int left;
	private int right;
	private int top;
	private int bottom;
	private boolean visible;
	private float[] color;
	private float lineWidth;

	public GWBorder(final int left1, final int right1, final int top1,
			final int bottom1) {
		this.left = left1;
		this.right = right1;
		this.top = top1;
		this.bottom = bottom1;
		visible = true;
		color = null;
		lineWidth = 1.5f;
	}

	/**
	 * @return the lineWidth
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	/**
	 * @param lineWidth1
	 *            the lineWidth to set
	 */
	public void setLineWidth(final float lineWidth1) {
		this.lineWidth = lineWidth1;
	}

	public GWBorder() {
		this(0, 0, 0, 0);
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible1
	 *            the visible to set
	 */
	public GWBorder setVisible(final boolean visible1) {
		this.visible = visible1;
		return this;
	}

	/**
	 * @return the color
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * @param color1
	 *            the color to set
	 */
	public void setColor(final float[] color1) {
		this.color = color1;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(final int left1) {
		this.left = left1;
	}

	public int getRight() {
		return right;
	}

	public void paint(final GL2 gl, final IGWidget w) {
		if (isVisible()) {
			float[] c = getColor();
			c = c == null ? w.getForeground() : c;
			if (c != null) {
				gl.glColor4fv(c, 0);
				gl.glLineWidth(getLineWidth());
				final int x = w.getX();
				final int y = w.getY();
				final int width = w.getWidth();
				final int height = w.getHeight();

				gl.glBegin(GL.GL_LINE_LOOP);

				gl.glVertex2i(x, y);
				gl.glVertex2i(x + width, y);
				gl.glVertex2i(x + width, y + height);
				gl.glVertex2i(x, y + height);

				gl.glEnd();
				gl.glLineWidth(1);
			}
		}
	}

	public void setRight(final int right1) {
		this.right = right1;
	}

	public int getTop() {
		return top;
	}

	public void setTop(final int top1) {
		this.top = top1;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(final int bottom1) {
		this.bottom = bottom1;
	}

	public GWBorder reset(final int left1, final int right1, final int top1,
			final int bottom1) {
		setLeft(left1);
		setRight(right1);
		setTop(top1);
		setBottom(bottom1);
		return this;
	}
}
