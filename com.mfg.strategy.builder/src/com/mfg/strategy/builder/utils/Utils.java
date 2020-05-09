/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
package com.mfg.strategy.builder.utils;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Color;

public class Utils {

	public static Color darker(Color c, double factor){
		Color newc = new Color(c.getDevice(), 
				(int)(factor*c.getRed()),
				(int)(factor*c.getGreen()),
						(int)(factor*c.getBlue()));
		return newc;
	}
	
	public static int[] parseIntArray(String array){
		StringTokenizer st = new StringTokenizer(
				array.substring(1, array.length()-1), ",");
		int n = st.countTokens();
		int[] res = new int[n];
		for (int i = 0; i < res.length; i++) {
			res[i] = Integer.parseInt(st.nextToken());
		}
		return res;
	}
	
	public static int get3Row(int p) {
		int c = 2 * p;
		return -1 - (int) Math.floor((1 + Math.sqrt(1 + 4 * c)) / (2.0));
	}

	public static int get3Col(int p) {
		int m = -(get3Row(p) + 2);
		return -p + m * (m + 1) / 2 - 1;
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(parseIntArray("[3,4,5]")));
	}
}
