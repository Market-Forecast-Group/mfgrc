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

import org.mfg.opengl.IGLConstants;

/**
 * @author arian
 *
 */
public interface IGWColorModel extends IGLConstants {
	/**
	 *
	 * @return
	 */
	public float[] getBackground();


	/**
	 *
	 * @return
	 */
	public float[] getForeground();

}