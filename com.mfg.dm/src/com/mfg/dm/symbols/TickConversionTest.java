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
package com.mfg.dm.symbols;

import static java.lang.System.out;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author arian
 * 
 * @deprecated on 2013/12/08, to be deleted.
 * 
 */
@Deprecated
public class TickConversionTest {

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		out.println(new BigDecimal("0.1"));
		out.println(new BigDecimal("0.10"));
		out.println(new BigDecimal("0.100"));
		System.exit(0);
		// real size 0.0112 size 112 scale 4
		test(0.10, 10, 2);
		test(0.112, 112, 3);
		test(0.0112, 112, 4);
		test(25.7, 257, 1);
		test(0, 0, 0);
		test(1, 1, 0);

		test1(0.0112000);
		test1(25.7);
		test1(25);
		test1(-10.1);

		test2(0.0112000);
		test2(25.7);
		test2(25);
		test2(-10.1);
		test2(-10.156789);
	}

	private static void test(double realTickSize, int intTickSize, int tickScale) {
		double realTickSize2 = getRealTickSize(intTickSize, tickScale);
		int intTickSize2 = getIntegerTickSize(realTickSize);
		int tickScale2 = getTickScale(realTickSize);

		assertTrue(realTickSize2 == realTickSize);
		assertTrue(intTickSize2 == intTickSize);
		assertTrue(tickScale2 == tickScale);
	}

	/**
	 * 
	 */
	private static void assertTrue(boolean expr) {
		if (!expr) {
			throw new AssertionError("Assertion violation");
		}
	}

	/**
	 * @param d
	 * @throws ParseException
	 */
	private static void test2(double realSize) throws ParseException {
		int integerTickSize = getIntegerTickSize(realSize);
		int tickScale = getTickScale(realSize);
		out.println("real size " + realSize + " size " + integerTickSize
				+ " scale " + tickScale);
		test(realSize, integerTickSize, tickScale);
	}

	public static double getRealTickSize(int intSize, int scale) {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		String intSizeStr = format.format(intSize);
		StringBuilder sb = new StringBuilder();
		for (int i = intSizeStr.length() - 1; i >= 0; i--) {
			char c = intSizeStr.charAt(i);
			if (c != ',') {
				sb.insert(0, c);
				if (sb.length() == scale) {
					sb.insert(0, ".");
				}
			}
		}
		while (sb.length() <= scale) {
			sb.insert(0, '0');
			if (sb.length() == scale) {
				sb.insert(0, ".");
			}
		}

		double realTickSize;
		try {
			out.println("computed real size " + sb);
			realTickSize = format.parse(sb.toString()).doubleValue();
			return realTickSize;
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static int getTickScale(double realSize) {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		format.setMaximumFractionDigits(10);
		String str = format.format(realSize);
		int dotIndex = str.indexOf('.');
		int scale = dotIndex == -1 ? 0 : str.length() - (dotIndex + 1);
		return scale;
	}

	public static int getIntegerTickSize(double realSize) {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		format.setMaximumFractionDigits(10);
		String str = format.format(realSize);
		String sizeStr = str.replace(".", "");
		int size;
		try {
			size = format.parse(sizeStr).intValue();
			return size;
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void test1(double realSize) throws ParseException {
		out.println("real size " + realSize);
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		format.setMaximumFractionDigits(10);
		String str = format.format(realSize);
		out.println("str rep " + str);
		String sizeStr = str.replace(".", "");
		out.println("size str " + sizeStr);
		int size = format.parse(sizeStr).intValue();
		out.println("size " + size);
		int dotIndex = str.indexOf('.');
		int scale = dotIndex == -1 ? 0 : str.length() - (dotIndex + 1);
		out.println("scale " + scale);
		out.println("--");
	}
}
