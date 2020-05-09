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
public interface IChannelCollection extends IItemCollection {
	IChannelCollection EMPTY = new IChannelCollection() {

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public double getStartTopPrice(int index) {
			return 0;
		}

		@Override
		public long getStartTime(int index) {
			return 0;
		}

		@Override
		public double getStartCenterPrice(int index) {
			return 0;
		}

		@Override
		public double getStartBottomPrice(int index) {
			return 0;
		}

		@Override
		public double getEndTopPrice(int index) {
			return 0;
		}

		@Override
		public long getEndTime(int index) {
			return 0;
		}

		@Override
		public double getEndCenterPrice(int index) {
			return 0;
		}

		@Override
		public double getEndBottomPrice(int index) {
			return 0;
		}
	};

	public long getStartTime(int index);

	public double getStartTopPrice(int index);

	public double getStartCenterPrice(int index);

	public double getStartBottomPrice(int index);

	public long getEndTime(int index);

	public double getEndTopPrice(int index);

	public double getEndCenterPrice(int index);

	public double getEndBottomPrice(int index);
}
