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

public interface IPivotModel {
	IPivotModel EMPTY = new IPivotModel() {

		@Override
		public int countNegPivots(int dataLayer, long lower, long upper) {
			return 0;
		}

		@Override
		public int getPivotsCount(int dataLayer) {
			return 0;
		}

		@Override
		public IPivotCollection getPivotAtIndex(int dataLayer, int index) {
			return IPivotCollection.EMPTY;
		}

		@Override
		public IPivotCollection getNegPivots(int dataLayer, long lowerTime,
				long upperTime) {
			return IPivotCollection.EMPTY;
		}

		@Override
		public int getLevel() {
			return 0;
		}

	};

	public int countNegPivots(int dataLayer, long lower, long upper);

	public int getPivotsCount(int dataLayer);

	public IPivotCollection getPivotAtIndex(int dataLayer, int index);

	IPivotCollection getNegPivots(int dataLayer, long lowerTime, long upperTime);
	
	public int getLevel();
}
