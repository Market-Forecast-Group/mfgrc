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

package com.mfg.chart.model;

public interface IBandsModel extends IScaleModel {
	public static final int MIN_SCALE_FILTERED = 7;
	public static final IBandsModel EMPTY = new IBandsModel() {

		@Override
		public int getLevel() {
			return 0;
		}

		@Override
		public IBandsCollection getBands(int dataLayer, long lowerTime,
				long upperTime) {
			return IBandsCollection.EMPTY;
		}

		@Override
		public boolean containsDataIn(int dataLayer, long lower, long upper) {
			return false;
		}
	};

	public IBandsCollection getBands(int dataLayer, long lowerTime,
			long upperTime);

	public boolean containsDataIn(int dataLayer, long lower, long upper);
}
