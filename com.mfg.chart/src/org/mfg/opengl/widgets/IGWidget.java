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

import java.util.List;

/**
 * @author arian
 * 
 */
public interface IGWidget {

	/**
	 * @return the colorModel
	 */
	public abstract IGWColorModel getColorModel();

	/**
	 * @param colorModel
	 *            the colorModel to set
	 */
	public abstract void setColorModel(IGWColorModel colorModel);

	/**
	 * @return the visible
	 */
	public abstract boolean isVisible();

	public abstract int getX();

	public abstract void setX(int x);

	public abstract int getY();

	public abstract void setY(int y);

	public abstract int getWidth();

	public abstract int getRight();

	public abstract int getLeft();

	public abstract int getUp();

	public abstract int getBottom();

	public abstract void setWidth(int width);

	public abstract int getBestWidth();

	public abstract void setBestWidth(Integer bestWidth);

	public abstract int getBestHeight();

	public abstract void setBestHeight(Integer bestHeight);

	public abstract GWLayout getLayout();

	public abstract void setLocation(int x, int y);

	public abstract void setLayout(GWLayout layout);

	public abstract GWBorder getBorder();

	public abstract void setBorder(GWBorder border);

	public abstract void setBounds(int x, int y, int width, int heigh);

	public abstract void setSize(int width, int height);

	public abstract float[] getBackground();

	public abstract float[] getForeground();

	public abstract float[] getForegroundToPaint();

	public abstract float[] getBackgroundToPaint();

	public abstract int getHeight();

	public abstract void setHeight(int height);

	public abstract void doLayout();

	public abstract IGWidget getParent();

	public abstract List<GWidget> getChildren();

	public void clearChildren();

	public abstract boolean contains(int x, int y);

	public void setVisibilityModel(IGWVisibilityModel visibilityModel);

	public void reshaped(int width, int height);

	public String getTooltip(int x, int y);
}
