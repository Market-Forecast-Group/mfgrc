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
public class GWVerticalLayout extends GWLineLayout {

	private boolean fill;


	public GWVerticalLayout() {
		this(0, true);
	}


	public GWVerticalLayout(final int gap, final boolean fill1) {
		super(gap);
		this.fill = fill1;
	}


	/**
	 * @return the fill
	 */
	public boolean isFill() {
		return fill;
	}


	/**
	 * @param fill1
	 *            the fill to set
	 */
	public void setFill(final boolean fill1) {
		this.fill = fill1;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.glwidgets.GWLayout#layoutWidget(org.mfg.glwidgets.GWidget)
	 */
	@Override
	public void layoutWidget(final IGWidget parent) {
		final GWBorder parentBorder = parent.getBorder();

		final int x = parent.getX() + parentBorder.getLeft();
		int y = parent.getY() + parentBorder.getTop();

		int parentH = 0;
		int fillW = 0;

		for (final IGWidget child : parent.getChildren()) {
			if (child.isVisible()) {
				child.setLocation(x, y);
				child.doLayout();

				final GWBorder childBorder = child.getBorder();

				final int h = child.getBestHeight() + childBorder.getTop() + childBorder.getBottom();
				final int w = child.getBestWidth() + childBorder.getLeft() + childBorder.getRight();

				child.setSize(w, h);

				final int incr = h + getGap();
				y += incr;
				parentH += incr;
				fillW = Math.max(fillW, w);
			}
		}

		parentH -= getGap();

		if (fill) {
			for (final IGWidget child : parent.getChildren()) {
				child.setWidth(fillW);
			}
		}

		parent.setBestHeight(Integer.valueOf(parentH));
		parent.setBestWidth(Integer.valueOf(fillW));
	}
}
