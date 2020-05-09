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
public class GWVisibilityModel implements IGWVisibilityModel {
	private boolean visible;


	public GWVisibilityModel(final boolean visible1) {
		super();
		this.visible = visible1;
	}


	@Override
	public boolean isVisible() {
		return visible;
	}


	public void setVisible(final boolean visible1) {
		this.visible = visible1;
	}

}
