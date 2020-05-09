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

package com.mfg.chart.model;

/**
 * @author arian
 * 
 */
public interface ITimePriceCollection {
	ITimePriceCollection EMPTY = new ITimePriceCollection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public long getTime(int index) {
			return 0;
		}

		@Override
		public double getPrice(int index) {
			return 0;
		}
	};

	public double getPrice(int index);

	public long getTime(int index);

	public int getSize();
}
