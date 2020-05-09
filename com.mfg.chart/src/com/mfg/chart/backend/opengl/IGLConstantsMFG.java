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

package com.mfg.chart.backend.opengl;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.IGLConstants;

/**
 * @author arian
 * 
 */
public interface IGLConstantsMFG extends IGLConstants {
	public static final byte[] BITMAP_FILTER = { 0, 24, 24, 24, 60, 126, 126,
			0, };
	public static final int BITMAP_FILTER_WIDTH = 8;
	public static final int BITMAP_FILTER_HEIGHT = 8;

	public static final byte[] BITMAP_PIVOTS = { 0, 48, 0, 120, 0, -4, 1, -2,
			3, -1, 0, 120, 0, 120, 30, 120, 30, 120, 30, 0, 30, 0, -1, -64,
			127, -128, 63, 0, 30, 0, 12, 0, };
	public static final int BITMAP_PIVOTS_WIDTH = 16;
	public static final int BITMAP_PIVOTS_HEIGHT = 16;

	public static final byte[] BITMAP_CHANNEL = { 1, -64, 1, -64, 1, -32, 28,
			112, 28, 56, 30, 28, 7, 15, -29, -121, -31, -57, -16, -32, 56, 120,
			28, 56, 14, 56, 7, -128, 3, -128, 3, -128, };
	public static final int BITMAP_CHANNELS_WIDTH = 16;
	public static final int BITMAP_CHANNELS_HEIGHT = 16;

	public static final byte[] BITMAP_BANDS = { 0, -64, 0, -64, 0, -64, 12,
			-64, 12, -1, 12, -1, 12, 3, 12, 3, -49, -13, -49, -13, -64, 48,
			-64, 48, -1, 48, -1, 0, 3, 0, 3, 0, };
	public static final int BITMAP_BANDS_WIDTH = 16;
	public static final int BITMAP_BANDS_HEIGHT = 16;

	public static final byte[] BITMAP_ZZ = { 0, 0, 0, 0, 8, 0, 28, 0, 62, 0,
			127, 0, -9, -125, -29, -57, -63, -17, 0, -2, 0, 124, 0, 56, 0, 16,
			0, 0, 0, 0, 0, 0, };
	public static final int BITMAP_ZZ_WIDTH = 16;
	public static final int BITMAP_ZZ_HEIGHT = 16;

	public static final BitmapData BITMAP_PROBS = new BitmapData(new byte[] {
			0, 0, 7, -16, 7, -16, 6, 48, 6, 48, 6, 55, 6, 55, -26, 54, -26, 54,
			102, 54, 102, 54, 126, 54, 126, 54, 0, 62, 0, 62, 0, 0, }, 16, 16);

	public static final int BITMAP_PROBS_WIDTH = 16;
	public static final int BITMAP_PROBS_HEIGHT = 16;

	public static final byte[] BITMAP_ON = { 63, -4, 127, -2, 96, 6, 96, 6, 96,
			6, 96, 6, 103, -26, 96, 6, 111, -10, 111, -10, 111, -10, 111, -10,
			96, 6, 127, -2, 63, -4, 0, 0, };
	public static final int BITMAP_ON_WIDTH = 16;
	public static final int BITMAP_ON_HEIGHT = 16;

	public static final byte[] BITMAP_OFF = { 63, -4, 127, -2, 96, 6, 111, -10,
			111, -10, 111, -10, 111, -10, 96, 6, 103, -26, 96, 6, 96, 6, 96, 6,
			96, 6, 127, -2, 63, -4, 0, 0, };
	public static final int BITMAP_OFF_WIDTH = 16;
	public static final int BITMAP_OFF_HEIGHT = 16;

	public static final byte[] BITMAP_OPEN_POSITION = { 0, 0, 0, 0, 127, -2,
			127, -2, 127, -2, 127, -2, 0, 0, 0, 0, };
	public static final int BITMAP_OPEN_POSITION_WIDTH = 16;
	public static final int BITMAP_OPEN_POSITION_HEIGHT = 8;

	public static final byte[] BITMAP_CLOSE_LONG_POSITION = { 0, 0, 63, -2, 31,
			-4, 15, -8, 7, -16, 3, -32, 1, -64, 0, -128, };
	public static final int BITMAP_CLOSE_LONG_POSITION_WIDTH = 16;
	public static final int BITMAP_CLOSE_LONG_POSITION_HEIGHT = 8;

	public static final byte[] BITMAP_CLOSE_SHORT_POSITION = { 60, 126, -1, -1,
			-1, -1, 126, 60, };
	public static final int BITMAP_CLOSE_SHORT_POSITION_WIDTH = 8;
	public static final int BITMAP_CLOSE_SHORT_POSITION_HEIGHT = 8;

	public static final BitmapData BITMAP_PIVOT_UP = new BitmapData(new byte[] {
			0, 1, -64, 0, 0, 1, -64, 0, 0, 1, -64, 0, 0, 1, -64, 0, 0, 1, -64,
			0, 0, 1, -64, 0, 0, 7, -16, 0, 0, 7, -16, 0, 0, 7, -16, 0, 0, 3,
			-32, 0, 0, 3, -32, 0, 0, 3, -32, 0, 0, 1, -64, 0, 0, 1, -64, 0, 0,
			0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, }, 32, 32);

	public static final BitmapData BITMAP_PIVOT_DOWN = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0, 1, -64, 0,
					0, 1, -64, 0, 0, 3, -32, 0, 0, 3, -32, 0, 0, 3, -32, 0, 0,
					7, -16, 0, 0, 7, -16, 0, 0, 7, -16, 0, 0, 1, -64, 0, 0, 1,
					-64, 0, 0, 1, -64, 0, 0, 1, -64, 0, 0, 1, -64, 0, 0, 1,
					-64, 0, }, 32, 32);

	public static final BitmapData BITMAP_PIVOT_RIGTH = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 7, -32, 0, 0, -1,
					-8, 0, 0, -1, -4, 0, 0, -1, -8, 0, 0, 7, -32, 0, 0, 7, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, 32, 32);

	public static final BitmapData BITMAP_PIVOT_LEFT = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, -64, 0, 0, 31, -64,
					0, 0, 127, -1, 0, 0, -1, -1, 0, 0, 127, -1, 0, 0, 31, -64,
					0, 0, 3, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, 32,
			32);

	public static final BitmapData BITMAP_SQUARE = new BitmapData(new byte[] {
			-1, -1, -1, -1, -1, -1, -1, -1, }, 8, 8, 4, 4);

	public static final BitmapData BITMAP_BIG_DOT = new BitmapData(new byte[] {
			0, 60, 126, 126, 126, 126, 60, 0, }, 8, 8, 4, 4);

	public static final BitmapData BITMAP_DOT = new BitmapData(new byte[] { 0,
			0, 24, 60, 60, 24, 0, 0, }, 8, 8);

	public static final BitmapData BITMAP_SNAPPING = new BitmapData(
			new byte[] { 0, 0, 1, -128, 1, -128, 1, -128, 1, -128, 1, -128, 0,
					0, 61, -68, 61, -68, 0, 0, 1, -128, 1, -128, 1, -128, 1,
					-128, 1, -128, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_TOOL = new BitmapData(new byte[] {
			-64, -96, 112, 46, 31, 25, 24, 12, }, 8, 8);

	public static final BitmapData BITMAP_REDO = new BitmapData(new byte[] { 0,
			0, 0, 0, 0, 0, 1, -128, 3, -128, 7, -128, 15, -16, 31, -8, 15, -4,
			7, -100, 3, -116, 1, -116, 0, 8, 0, 0, 0, 0, 0, 0, }, 16, 16);

	// public static final BitmapData BITMAP_CLOSE = new BitmapData(new byte[] {
	// 0,102,126,60,60,126,102,0,}, 8, 8);

	public static final BitmapData BITMAP_PREV_PAGE = new BitmapData(
			new byte[] { 0, 0, 0, 0, 3, -64, 3, -64, 3, -64, 3, -64, 3, -64,
					31, -8, 31, -8, 15, -16, 7, -32, 3, -64, 1, -128, 0, 0, 0,
					0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_SAVE = new BitmapData(new byte[] { 0,
			0, -25, -25, -25, -25, -25, -25, -25, -25, -25, -25, -25, -25, -32,
			7, -16, 15, -1, -1, -1, -1, -16, 15, -16, 15, -13, -49, 115, -50,
			51, -52, }, 16, 16);

	// public static final BitmapData BITMAP_SAVE = new BitmapData(new byte[] {
	// 0,
	// 0, 0, 0, 0, 0, 27, -40, 27, -40, 27, -40, 27, -40, 24, 24, 31, -8,
	// 24, 24, 27, -40, 27, -40, 11, -48, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_NEW = new BitmapData(new byte[] { 0,
			0, 63, -16, 64, 8, 64, 8, 64, 8, 95, -24, 64, 8, 64, 8, 64, 8, 95,
			-32, 64, 12, 64, 12, 64, 63, 63, -65, 0, 12, 0, 12, }, 16, 16);

	public static final BitmapData BITMAP_PIN = new BitmapData(new byte[] { 0,
			0, 96, 0, 80, -128, 41, -128, 19, -128, 7, -128, 15, -64, 31, -32,
			63, -2, 3, -4, 1, -8, 0, -12, 0, -28, 0, -40, 0, -128, 0, 0, }, 16,
			16);

	// public static final BitmapData BITMAP_NEW = new BitmapData(new byte[] {
	// 0,
	// 0, 0, 0, 31, -64, 32, 32, 32, 32, 39, 32, 32, 32, 39, 0, 32, 48,
	// 32, 48, 30, -4, 0, -4, 0, 48, 0, 48, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_DELETE = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 24, 24, 28, 56, 14, 112, 7, -32, 3, -64, 3, -64,
			7, -32, 14, 112, 28, 56, 24, 24, 0, 0, 0, 0, 0, 0, }, 16, 16);

	// public static final BitmapData BITMAP_DELETE = new BitmapData(new byte[]
	// {
	// 0, 0, 0, 0, 0, 0, 0, 0, 12, 48, 14, 112, 7, -32, 3, -64, 3, -64, 7,
	// -32, 14, 112, 12, 48, 0, 0, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_AUTORANGE = new BitmapData(
			new byte[] { 0, 0, 42, -86, 85, 84, 42, -86, 0, 0, 64, 0, 102, 16,
					63, 56, 25, -20, 0, -58, 0, 2, 0, 0, 85, 84, 42, -86, 85,
					84, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_PRICES = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 4, -64, 14, 102, 27, 63, 49,
			25, -32, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_BACKGROUND = new BitmapData(
			new byte[] { 0, 0, 0, 0, 63, -4, 45, -76, 63, -4, 63, -4, 45, -76,
					63, -4, 63, -4, 45, -76, 63, -4, 63, -4, 45, -76, 63, -4,
					0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_GRID = new BitmapData(new byte[] { 0,
			0, 0, 0, 0, 0, 18, 72, 0, 0, 0, 0, 18, 72, 0, 0, 0, 0, 18, 72, 0,
			0, 0, 0, 18, 72, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_TEXT = new BitmapData(new byte[] { 0,
			0, 0, 0, 0, -128, 48, 0, 34, -88, 16, 0, 48, -128, 0, 0, 0, -128,
			16, 0, 18, -88, 48, 0, 16, -128, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_SNAPSHOT = new BitmapData(new byte[] {
			0, 0, 0, 16, 0, 0, 0, 16, 64, 16, 97, -128, 51, -48, 30, 64, 12,
			118, 0, 47, 0, 25, 0, 0, 0, 16, 0, 0, 0, 16, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_CLOSE_POSITIONS_SWTICH = new BitmapData(
			new byte[] { 0, 0, 0, 0, 127, -8, 63, -16, 31, -32, 15, -64, 23,
					-128, 3, 0, 64, 56, 0, 124, 0, -2, -86, -2, 0, -2, 0, 124,
					0, 56, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_BIG_FILTER = new BitmapData(
			new byte[] { 0, 0, 0, 0, 3, -64, 3, -64, 3, -64, 3, -64, 3, -64, 7,
					-32, 15, -16, 31, -8, 63, -4, 127, -2, 127, -2, 0, 0, 0, 0,
					0, 0, }, 16, 16);
	public static final BitmapData BITMAP_TREND_ICON = new BitmapData(
			new byte[] { 0, 0, 0, 0, 1, -32, 1, -32, 1, -32, 1, -32, 15, -4, 7,
					-8, 3, -16, 1, -32, 64, -64, 124, 0, 63, -64, 3, -2, 0, 62,
					0, 0, }, 16, 16);
	public static final BitmapData BITMAP_TRADING_TOOL_ICON = new BitmapData(
			new byte[] { 0, 0, 0, 0, 8, 0, 28, 0, 62, 0, 127, 0, 28, 56, 28,
					56, 28, 56, 28, 56, 0, -2, 0, 124, 0, 56, 0, 16, 0, 0, 0,
					0, }, 16, 16);

	public static final BitmapData BITMAP_ANCHOR_LINES_TOOL_ICON = new BitmapData(
			new byte[] { 0, 0, 1, -128, 15, -16, 31, -8, 49, -116, 33, -124,
					33, -124, 113, -114, 33, -124, 1, -128, 1, -128, 1, -128,
					2, 64, 2, 64, 1, -128, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_AUTO_ANCHOR_LINES_TOOL_ICON = new BitmapData(
			new byte[] { 0, 0, 16, 0, 124, 0, -110, 0, -110, 0, 16, 0, 16, 16,
					40, 124, 16, -110, 0, -110, 0, 16, 0, 16, 0, 16, 0, 40, 0,
					16, 0, 0, }, 16, 16);

	public static final BitmapData BITMAP_HARMONIC_LINES_TOOL_ICON = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, -4, 0, 0, 72, 0, 72, 0,
					121, -4, 72, 0, 72, 0, 0, 0, 1, -4, 0, 0, 0, 0, 0, 0, },
			16, 16);

	public static final BitmapData SHAPE_RECT_16 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -32, 7, -32, 7, -32, 7, -32, 7,
			-32, 7, -32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData SHAPE_CIRCLE_16 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 7, -128, 15, -64, 15, -64, 15,
			-64, 7, -128, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData SHAPE_TRIANGLE_16 = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 127, -2, 63, -4, 31,
					-8, 15, -16, 7, -32, 3, -64, 1, -128, 0, 0, 0, 0, 0, 0, 0,
					0, }, 16, 16);
	public static final BitmapData SHAPE_BAR_16 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -4, 63, -4, 63, -4, 63, -4,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData SHAPE_RECT_24 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 31, -8, 31, -8, 31, -8, 31, -8, 31, -8, 31, -8,
			31, -8, 31, -8, 31, -8, 31, -8, 0, 0, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData SHAPE_TRIANGLE_24 = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15,
					-1, -1, -32, 7, -1, -1, -64, 3, -1, -1, -128, 1, -1, -1, 0,
					0, -1, -2, 0, 0, 127, -4, 0, 0, 63, -8, 0, 0, 31, -16, 0,
					0, 15, -32, 0, 0, 7, -64, 0, 0, 3, -128, 0, 0, 1, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, }, 16, 16);

	public static final BitmapData SHAPE_CIRCLE_24 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 7, -64, 15, -32, 31, -16, 63, -8, 63, -8, 63, -8,
			63, -8, 63, -8, 31, -16, 15, -32, 7, -64, 0, 0, 0, 0, }, 16, 16);

	public static final BitmapData SHAPE_BAR_24 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 15, -1, -1, -16, 15, -1, -1, -16, 15, -1, -1, -16, 15,
			-1, -1, -16, 15, -1, -1, -16, 15, -1, -1, -16, 15, -1, -1, -16, 15,
			-1, -1, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, }, 32, 32);

	public static final BitmapData SHAPE_RECT_32 = new BitmapData(new byte[] {
			0, 0, 127, -2, 127, -2, 127, -2, 127, -2, 127, -2, 127, -2, 127,
			-2, 127, -2, 127, -2, 127, -2, 127, -2, 127, -2, 127, -2, 127, -2,
			0, 0, }, 16, 16);

	public static final BitmapData SHAPE_CIRCLE_32 = new BitmapData(
			new byte[] { 3, -64, 15, -16, 31, -8, 63, -4, 63, -4, 127, -2, 127,
					-2, 127, -2, 127, -2, 127, -2, 127, -2, 63, -4, 63, -4, 31,
					-8, 15, -16, 3, -64, }, 16, 16);

	public static final BitmapData SHAPE_TRIANGLE_32 = new BitmapData(
			new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63,
					-1, -1, -4, 31, -1, -1, -8, 15, -1, -1, -16, 7, -1, -1,
					-32, 3, -1, -1, -64, 1, -1, -1, -128, 0, -1, -1, 0, 0, 127,
					-2, 0, 0, 63, -4, 0, 0, 31, -8, 0, 0, 15, -16, 0, 0, 7,
					-32, 0, 0, 3, -64, 0, 0, 1, -128, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, }, 32, 32);

	public static final BitmapData SHAPE_BAR_32 = new BitmapData(new byte[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			127, -1, -1, -2, 127, -1, -1, -2, 127, -1, -1, -2, 127, -1, -1, -2,
			127, -1, -1, -2, 127, -1, -1, -2, 127, -1, -1, -2, 127, -1, -1, -2,
			127, -1, -1, -2, 127, -1, -1, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 32, 32);

	/**
	 * <pre>
	 * 
	 * SHAPES[shapeWidth][shapeType]
	 * shapeType = 0 .. 3 (bar, circle, rect, triangle)
	 * shapeSize = 0 .. 2 (16, 25, 32)
	 * 
	 * </pre>
	 */
	public static final BitmapData[][] SHAPES = {
			{ SHAPE_BAR_16, SHAPE_CIRCLE_16, SHAPE_RECT_16, SHAPE_TRIANGLE_16 },
			{ SHAPE_BAR_24, SHAPE_CIRCLE_24, SHAPE_RECT_24, SHAPE_TRIANGLE_24 },
			{ SHAPE_BAR_32, SHAPE_CIRCLE_32, SHAPE_RECT_32, SHAPE_TRIANGLE_32 } };

}
