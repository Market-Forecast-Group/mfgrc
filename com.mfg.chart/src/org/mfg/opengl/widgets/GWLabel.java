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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author arian
 * 
 */
public class GWLabel extends GWidget {
	private String text;
	private int font;
	private final GLUT glut;
	private String tooltip;

	public GWLabel(final GWidget parent, final String text1) {
		super(parent);
		font = GLUT.BITMAP_HELVETICA_18;
		glut = new GLUT();

		getBorder().reset(10, 10, 10, 10);
		setText(text1);
	}

	public GWLabel(final GWidget parent) {
		this(parent, "");
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
	 * @return the font
	 */
	public int getFont() {
		return font;
	}

	/**
	 * @param font1
	 *            the font to set
	 */
	public void setFont(final int font1) {
		this.font = font1;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text1) {
		this.text = text1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#paintWidget(javax.media.opengl.GL)
	 */
	@Override
	protected void paintWidget(final GL2 gl) {
		super.paintWidget(gl);
		paintLabelText(gl);
	}

	protected void paintLabelText(final GL2 gl) {
		gl.glColor4fv(getForegroundToPaint(), 0);

		final int y = getY() + (getHeight() - getBestHeight()) / 2 - 1;
		int width = getWidth();
		int bestWidth = getBestWidth();
		int x = getX() + (width - bestWidth) / 2 - 1;
		x += (bestWidth - getTextWidth()) / 2;
		gl.glRasterPos2i(x, y);

		glut.glutBitmapString(getFont(), getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#doLayout()
	 */
	@Override
	public void doLayout() {
		setBestWidth(Integer.valueOf(getTextWidth()));
		setBestHeight(Integer.valueOf(14));
		super.doLayout();
	}

	/**
	 * @return
	 */
	public final int getTextWidth() {
		return glut.glutBitmapLength(getFont(), getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.GWidget#init(javax.media.opengl.GL)
	 */
	@Override
	public void init(final GL2 gl) {
		//Adding a comment to avoid empty block warning.
	}
}
