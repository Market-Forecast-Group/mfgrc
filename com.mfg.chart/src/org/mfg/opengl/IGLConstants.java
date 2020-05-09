package org.mfg.opengl;

public interface IGLConstants {
	short STIPPLE_PATTERN = (short) 0xAAAA;

	int STIPPLE_FACTOR_NULL = 0;
	int STIPPLE_FACTOR_1 = 1;
	int STIPPLE_FACTOR_2 = 3;
	int STIPPLE_FACTOR_3 = 5;
	int STIPPLE_FACTOR_4 = 7;
	int STIPPLE_FACTOR_5 = 9;
	int STIPPLE_FACTOR_6 = 11;
	int STIPPLE_FACTOR_7 = 13;
	int STIPPLE_FACTOR_8 = 15;

	int[] STIPPLE_FACTORS = { STIPPLE_FACTOR_NULL, STIPPLE_FACTOR_1,
			STIPPLE_FACTOR_2, STIPPLE_FACTOR_3, STIPPLE_FACTOR_4,
			STIPPLE_FACTOR_5, STIPPLE_FACTOR_6, STIPPLE_FACTOR_7,
			STIPPLE_FACTOR_8 };

	float[] COLOR_BLACK = { 0, 0, 0, 1 };

	float[] COLOR_WHITE = { 1, 1, 1, 1 };

	float[] COLOR_LIGHT_GRAY = { 0.75f, 0.75f, 0.75f, 1 };
	float[] COLOR_GRAY = { 0.5f, 0.5f, 0.5f, 1 };
	float[] COLOR_DARK_GRAY = { 0.2f, 0.2f, 0.2f, 1 };

	float[] COLOR_RED = { 1, 0, 0, 1 };
	float[] COLOR_DARK_RED = GLHelper.darker(COLOR_RED);

	float[] COLOR_PINK = { 1, 0.68359375f, 0.68359375f, 1 };

	float[] COLOR_GREEN = { 0, 1, 0, 1 };
	float[] COLOR_DARK_GREEN = GLHelper.darker(COLOR_GREEN);

	float[] COLOR_YELLOW = { 1, 1, 0, 1 };
	float[] COLOR_BLUE = { 0, 0, 1, 1 };
	float[] COLOR_DARK_BLUE = GLHelper.darker(COLOR_BLUE);

	float[] COLOR_MAGNETA = { 1, 0, 1, 1 };

	float[] COLOR_ORANGE = { 1, 0.78f, 0, 1 };

	float[] COLOR_CYAN = { 0, 1, 1, 1 };
	float[] COLOR_PURPLE = { 0.63f, 0.28f, 0.64f, 1 };

	float[][] COLORS = { COLOR_BLACK, COLOR_WHITE, COLOR_LIGHT_GRAY,
			COLOR_GRAY, COLOR_DARK_GRAY, COLOR_RED, COLOR_DARK_RED, COLOR_PINK,
			COLOR_YELLOW, COLOR_ORANGE, COLOR_GREEN, COLOR_DARK_GREEN,
			COLOR_MAGNETA, COLOR_CYAN, COLOR_BLUE, COLOR_DARK_BLUE,
			COLOR_PURPLE };
}
