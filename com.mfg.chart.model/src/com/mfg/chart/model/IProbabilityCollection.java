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
public interface IProbabilityCollection extends IItemCollection {

	IProbabilityCollection EMPTY = new IProbabilityCollection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public boolean isPositiveTradeDireaction(int index) {
			return false;
		}

		@Override
		public long getTime(int index) {
			return 0;
		}

		@Override
		public double getPositivePrice(int index) {
			return 0;
		}

		@Override
		public double getNegativePrice(int index) {
			return 0;
		}
	};

	public long getTime(int index);

	public double getPositivePrice(int index);

	public double getNegativePrice(int index);

	public boolean isPositiveTradeDireaction(int index);
}
