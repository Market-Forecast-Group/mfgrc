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

package org.mfg.opengl;

import javax.media.opengl.GL2;

public interface IGLDrawable extends IGLConstants {

	public void init(GL2 gl);

	public void reshape(GL2 gl, int width, int height);

	public void display(GL2 gl, int width, int height);

}
