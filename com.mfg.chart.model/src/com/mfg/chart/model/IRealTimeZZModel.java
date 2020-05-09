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

package com.mfg.chart.model;

/**
 * @author arian
 * 
 */
public interface IRealTimeZZModel {

	public static final IRealTimeZZModel EMPTY = new IRealTimeZZModel() {

		@Override
		public boolean isCompleted(int dataLayer) {
			return false;
		}

		@Override
		public long getTime1(int dataLayer) {
			return 0;
		}

		@Override
		public long getTime2(int dataLayer) {
			return 0;
		}

		@Override
		public double getPrice1(int dataLayer) {
			return 0;
		}

		@Override
		public double getPrice2(int dataLayer) {
			return 0;
		}

		@Override
		public double getTHPrice(int dataLayer) {
			return 0;
		}

		@Override
		public double getTHTime(int dataLayer) {
			return 0;
		}

		@Override
		public double[] getPercentilStatistics(int dataLayer, int scale) {
			return null;
		}

	};

	public boolean isCompleted(int dataLayer);

	public long getTime1(int dataLayer);

	public long getTime2(int dataLayer);

	public double getPrice1(int dataLayer);

	public double getPrice2(int dataLayer);

	public double getTHPrice(int dataLayer);

	public double getTHTime(int dataLayer);

	public double[] getPercentilStatistics(int dataLayer, int scale);
}
