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

/**
 * @author arian
 * 
 */
public class GWHorizontalLayout extends GWLineLayout {

	private boolean fill;

	public GWHorizontalLayout() {
		this(0, true);
	}

	public GWHorizontalLayout(final int gap, final boolean fill1) {
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

		int x = parent.getX() + parentBorder.getLeft();
		final int y = parent.getY() + parentBorder.getTop();

		int parentW = 0;
		int fillH = 0;

		for (final IGWidget child : parent.getChildren()) {
			if (child.isVisible()) {
				child.setLocation(x, y);
				child.doLayout();

				final GWBorder childBorder = child.getBorder();

				final int h = child.getBestHeight() + childBorder.getTop()
						+ childBorder.getBottom();
				final int w = child.getBestWidth() + childBorder.getLeft()
						+ childBorder.getRight();

				child.setSize(w, h);

				final int incr = w + getGap();
				x += incr;
				parentW += incr;
				fillH = Math.max(fillH, h);

				afterLayout(child);
			}
		}

		parentW -= getGap();

		if (fill) {
			for (final IGWidget child : parent.getChildren()) {
				child.setHeight(fillH);
			}
		}

		parent.setBestHeight(Integer.valueOf(fillH));
		parent.setBestWidth(Integer.valueOf(parentW));
	}
}
