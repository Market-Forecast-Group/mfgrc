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
public interface ITimesOfTheDayCollection {
	ITimesOfTheDayCollection EMPTY = new ITimesOfTheDayCollection() {

		@Override
		public long getTime(int index) {
			return 0;

		}

		@Override
		public String getLabel(int index) {
			return null;
		}

		@Override
		public int getSize() {
			return 0;
		}
	};

	public int getSize();

	public long getTime(int index);

	public String getLabel(int index);

}
