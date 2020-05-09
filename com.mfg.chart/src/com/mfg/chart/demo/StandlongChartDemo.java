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

package com.mfg.chart.demo;

import java.io.File;
import java.io.IOException;

import com.mfg.chart.ui.IChartUtils;

public class StandlongChartDemo {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		IChartUtils.openStandAlongChartWindow(
				new File("c:/users/arian/Desktop"), new File(
						"C:/Users/Arian/Desktop/ChartData.mfgchart"), false);
	}
}
