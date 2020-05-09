package com.mfg.utils.ui;

import org.eclipse.swt.graphics.RGB;

import com.mfg.utils.ui.ColorModelComposite.IColorModel;

public class RGBModel implements IColorModel {
	private static final String[] NAMES = { "Red", "Green", "Blue" };
	private static final int[] SIZES = { 255, 255, 255 };

	public RGBModel() {
		super();
	}

	@Override
	public RGB getColor(int x, int y, int z) {
		return new RGB(x, y, z);
	}

	@Override
	public RGB getSliderColor(int progress, int selectedComp, int x, int y,
			int z) {
		RGB color;
		switch (selectedComp) {
		case 0:
			color = getColor(progress, 0, 0);
			break;
		case 1:
			color = getColor(0, progress, 0);
			break;
		default:
			color = getColor(0, 0, progress);
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

	@Override
	public int[] getComponents(RGB color) {
		return new int[] { color.red, color.green, color.blue };
	}
}