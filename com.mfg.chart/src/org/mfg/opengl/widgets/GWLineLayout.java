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
public abstract class GWLineLayout extends GWLayout {
	private int gap;

	public GWLineLayout(final int gap1) {
		this.gap = gap1;
	}

	public GWLineLayout() {
		gap = 0;
	}

	public int getGap() {
		return gap;
	}

	public void setGap(final int gap1) {
		this.gap = gap1;
	}

	/**
	 * @param widget  
	 */
	protected void afterLayout(IGWidget widget) {
		//Adding a comment to avoid empty block warning.
	}

}
