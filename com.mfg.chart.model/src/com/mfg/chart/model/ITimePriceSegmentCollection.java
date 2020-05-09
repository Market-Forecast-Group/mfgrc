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
public interface ITimePriceSegmentCollection {
	public ITimePriceSegmentCollection EMPTY = new ITimePriceSegmentCollection() {

		@Override
		public long getTime1(int index) {
			return 0;
		}

		@Override
		public long getTime0(int index) {
			return 0;
		}

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public double getPrice1(int index) {
			return 0;
		}

		@Override
		public double getPrice0(int index) {
			return 0;
		}
	};

	public int getSize();

	public long getTime0(int index);

	public long getTime1(int index);

	public double getPrice0(int index);

	public double getPrice1(int index);
}
