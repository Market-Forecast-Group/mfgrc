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
package org.mfg.opengl.tools.bmpCreator;

import static java.lang.System.out;

import org.mfg.opengl.widgets.GWBitmap;
import org.mfg.opengl.widgets.GWColorModel;
import org.mfg.opengl.widgets.GWLabel;
import org.mfg.opengl.widgets.GWRoot;
import org.mfg.opengl.widgets.GWidget;

/**
 * @author arian
 * 
 */
public class GWBitmapCreator extends GWRoot {

	public static byte[] parseBooleanBitmap(final boolean[][] bmp) {
		final int w = bmp.length;
		final int h = bmp[0].length;

		final String[][] strBytes = new String[w][h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				String str = strBytes[x / 8][y];
				str = str == null ? "" : str;
				str += bmp[x][y] ? "1" : "0";
				strBytes[x / 8][y] = str;
			}
		}

		final byte[] bytes = new byte[h * w / 8];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w / 8; x++) {
				bytes[y * w / 8 + x] = (byte) Integer.parseInt(strBytes[x][y],
						2);
			}
		}

		out.print("int BMP_WIDTH = " + w + ";\n");
		out.print("int BMP_HEIGHT = " + h + ";\n");
		out.print("byte[] BMP = { ");
		for (final byte b : bytes) {
			out.print(b + ",");
		}
		out.println("};");
		out.println("");

		return bytes;
	}

	/**
	 * @author arian
	 * 
	 */
	private final class GWCell extends GWLabel {
		private final int _cellY;
		private final int _cellX;

		GWCell(final GWidget parent, final int cellX, final int cellY) {
			super(parent, "");
			this._cellY = cellY;
			this._cellX = cellX;
		}

		@Override
		public void doLayout() {
			//
		}

		@Override
		public int getX() {
			return 20 + _cellX * CELL_SIZE;
		}

		@Override
		public int getY() {
			return _cellY * CELL_SIZE + GWBitmapCreator.this.getHeight() / 2
					- getBmpHeight() * CELL_SIZE / 2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mfg.opengl.widgets.GWidget#getBackground()
		 */
		@Override
		public float[] getBackground() {
			final boolean value = _bmp[_cellX][_cellY];
			return value ? _selectedColor.getBackground() : _blankColor
					.getBackground();
		}

		@Override
		public void mouseClicked(final int x, final int y) {
			final boolean selected = _bmp[_cellX][_cellY];
			_bmp[_cellX][_cellY] = !selected;
			doRepaint();
		}

	}

	final boolean[][] _bmp;
	final GWLabel _dimLabel;
	final GWColorModel _selectedColor;
	final GWColorModel _blankColor;
	private final Runnable _repaint;
	private final GWBitmap resultBmp;
	private static final int CELL_SIZE = 20;

	/**
	 * @param bmp
	 */
	public GWBitmapCreator(final boolean[][] bmp, final Runnable repaint) {
		this._bmp = bmp;
		this._repaint = repaint;
		_dimLabel = new GWLabel(this, getBmpWidth() + "x" + getBmpHeight());
		_dimLabel.setLocation(10, 10);
		_dimLabel.getBorder().setVisible(false);

		_selectedColor = new GWColorModel(COLOR_WHITE, COLOR_ORANGE);
		_blankColor = new GWColorModel(COLOR_WHITE, COLOR_BLACK);

		for (int x = 0; x < getBmpWidth(); x++) {
			for (int y = 0; y < getBmpHeight(); y++) {
				final GWLabel cell = new GWCell(this, x, y);
				cell.setColorModel(_blankColor);
				cell.setWidth(CELL_SIZE);
				cell.setHeight(CELL_SIZE);
				cell.setY(y * CELL_SIZE);
			}
		}

		resultBmp = new GWBitmap(this, new byte[getBmpWidth() / 8
				* getBmpHeight()], getBmpWidth(), getBmpHeight()) {
			@Override
			public int getX() {
				return 10 + _dimLabel.getRight();
			}

			@Override
			public int getY() {
				return _dimLabel.getY() + _dimLabel.getHeight() / 2
						- getHeight() / 2;
			}
		};
		resultBmp.setColorModel(new GWColorModel(COLOR_ORANGE, COLOR_BLACK));
		resultBmp.getBorder().setVisible(false);

	}

	public int getBmpWidth() {
		return _bmp.length;
	}

	public int getBmpHeight() {
		return _bmp[0].length;
	}

	public byte[] getConvertedBitmap() {
		return parseBooleanBitmap(_bmp);
	}

	void doRepaint() {
		final byte[] bytes = parseBooleanBitmap(_bmp);
		resultBmp.setBmp(bytes, getBmpWidth(), getBmpHeight());

		_repaint.run();
	}
}
