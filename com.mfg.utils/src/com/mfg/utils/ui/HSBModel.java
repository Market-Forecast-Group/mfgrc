package com.mfg.utils.ui;

import org.eclipse.swt.graphics.RGB;

import com.mfg.utils.ui.ColorModelComposite.IColorModel;

public class HSBModel implements IColorModel {

	private static final String[] NAMES = { "Hue", "Saturation", "Brightness" };
	private static final int[] SIZES = { 360, 100, 100 };

	public HSBModel() {
		super();
	}

	@Override
	public RGB getColor(int x, int y, int z) {
		float h = (float) x / 360;
		float s = (float) y / 100;
		float b = (float) z / 100;

		java.awt.Color color = java.awt.Color.getHSBColor(h, s, b);

		return new RGB(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	public int[] getComponents(RGB color) {
		float[] hsb = new float[3];
		java.awt.Color.RGBtoHSB(color.red, color.green,
				color.blue, hsb);
		return new int[] { (int) (hsb[0] * 360), (int) (hsb[1] * 100),
				(int) (hsb[2] * 100) };
	}

	@Override
	public RGB getSliderColor(int progress, int selectedComp, int x, int y,
			int z) {
		RGB color;
		switch (selectedComp) {
		case 0:
			color = getColor(progress, 100, 100);
			break;
		case 1:
			color = getColor(x, progress, 100);
			break;
		default:
			color = getColor(x, y, progress);
			break;
		}
		return color;
	}

	@Override
	public String[] getComponentNames() {
		return NAMES;
	}

	@Override
	public int[] getComponentSizes() {
		return SIZES;
	}

}