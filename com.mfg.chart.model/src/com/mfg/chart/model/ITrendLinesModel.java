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
public interface ITrendLinesModel {
	public ITrendLinesModel EMPTY = new ITrendLinesModel() {

		@Override
		public ITimePriceCollection getUpLine(int dataLayer, int level) {
			return ITimePriceCollection.EMPTY;
		}

		@Override
		public ITimePriceCollection getDownLine(int dataLayer, int level) {
			return ITimePriceCollection.EMPTY;
		}

		@Override
		public ITimePriceCollection getDashedLine(int dataLayer, int level) {
			return ITimePriceCollection.EMPTY;
		}
	};

	public ITimePriceCollection getUpLine(int dataLayer, int level);

	public ITimePriceCollection getDownLine(int dataLayer, int level);

	public ITimePriceCollection getDashedLine(int dataLayer, int level);
}
