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

public interface IProbabilityBandsCollection extends IItemCollection {

	IProbabilityBandsCollection EMPTY = new IProbabilityBandsCollection() {

		@Override
		public int getSize() {
			return 0;
		}


		@Override
		public long getTime(int aIndex) {
			return 0;
		}


		@Override
		public double getPositiveTargetPrice(int aIndex) {
			return 0;
		}


		@Override
		public double getNegativeTargetPrice(int aIndex) {
			return 0;
		}
	};


	public double getPositiveTargetPrice(int index);


	public double getNegativeTargetPrice(int index);


	public long getTime(int index);
}
