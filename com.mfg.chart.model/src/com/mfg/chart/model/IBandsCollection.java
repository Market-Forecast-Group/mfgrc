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

public interface IBandsCollection extends IItemCollection {

	IBandsCollection EMPTY = new IBandsCollection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public double getTopPrice(int index) {
			return 0;
		}

		@Override
		public long getTime(int index) {
			return 0;
		}

		@Override
		public double getCenterPrice(int index) {
			return 0;
		}

		@Override
		public double getBottomPrice(int index) {
			return 0;
		}

		@Override
		public double getTopRaw(int index) {
			return 0;
		}

		@Override
		public double getCenterRaw(int index) {
			return 0;
		}

		@Override
		public double getBottomRaw(int index) {
			return 0;
		}
	};

	public double getTopPrice(int index);

	public double getCenterPrice(int index);

	public double getBottomPrice(int index);

	public double getTopRaw(int index);

	public double getCenterRaw(int index);

	public double getBottomRaw(int index);

	public long getTime(int index);
}
