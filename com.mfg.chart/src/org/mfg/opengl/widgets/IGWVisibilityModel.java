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
public interface IGWVisibilityModel {
	IGWVisibilityModel HIDDEN = new IGWVisibilityModel() {

		@Override
		public boolean isVisible() {
			return false;
		}
	};

	IGWVisibilityModel VISIBLE = new IGWVisibilityModel() {

		@Override
		public boolean isVisible() {
			return true;
		}
	};


	public boolean isVisible();
}
