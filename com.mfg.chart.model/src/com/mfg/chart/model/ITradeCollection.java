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
public interface ITradeCollection extends IItemCollection {
	ITradeCollection EMPTY = new ITradeCollection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public long getOpenTime(int index) {
			return 0;
		}

		@Override
		public double getOpenPrice(int index) {
			return 0;
		}

		@Override
		public long[] getOpenings(int index) {
			return null;
		}

		@Override
		public long getCloseTime(int index) {
			return 0;
		}

		@Override
		public double getClosePrice(int index) {
			return 0;
		}

		@Override
		public boolean isGain(int index) {
			return false;
		}

		@Override
		public boolean isLong(int index) {
			return false;
		}

		@Override
		public boolean isClosed(int index) {
			return false;
		}
	};

	public long getOpenTime(int index);

	public double getOpenPrice(int index);

	public long[] getOpenings(int index);

	public long getCloseTime(int index);

	public double getClosePrice(int index);

	public boolean isGain(int index);

	public boolean isLong(int index);

	public boolean isClosed(int index);
}
