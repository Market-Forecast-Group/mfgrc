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

public interface IPivotCollection extends ITimePriceCollection {
	public IPivotCollection EMPTY = new IPivotCollection() {

		@Override
		public long getTime(int index) {
			return 0;
		}

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public double getPrice(int index) {
			return 0;
		}

		@Override
		public boolean isUp(int index) {
			return false;
		}

		@Override
		public long getTHTime(int index) {
			return 0;
		}

		@Override
		public double getTHPrice(int index) {
			return 0;
		}
	};

	public boolean isUp(int index);

	public long getTHTime(int index);

	public double getTHPrice(int index);
}
