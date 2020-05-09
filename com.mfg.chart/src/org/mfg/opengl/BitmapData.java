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

package org.mfg.opengl;

/**
 * @author arian
 *
 */
public class BitmapData {
	public byte[] bitmap;
	public int width;
	public int height;
	public int x;
	public int y;

	public BitmapData(final byte[] bmp, final int width1, final int height1, final int x1, final int y1) {
		super();
		bitmap = bmp;
		this.width = width1;
		this.height = height1;
		this.x = x1;
		this.y = y1;
	}

	public BitmapData(final byte[] bmp, final int width1, final int height1) {
		this(bmp, width1, height1, width1 / 2, height1 / 2);
	}
}
