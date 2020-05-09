/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision$: $Date$:
 * $Id$:
 */
package org.mfg.opengl;

/**
 * @author arian
 *
 */
public class GLHelper {
	public static float[] darker(final float[] color) {
		final float r = color[0] * 0.7f;
		final float g = color[1] * 0.7f;
		final float b = color[2] * 0.7f;
		final float a = color[3];
		return new float[] { r, g, b, a };
	}
}
