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
public interface IRealTimeChannelModel {
	public IRealTimeChannelModel EMPTY = new IRealTimeChannelModel() {

		@Override
		public long getStartTime(int dataLayer) {
			return 0;
		}

		@Override
		public double getStartTopPrice(int dataLayer) {
			return 0;
		}

		@Override
		public double getStartCenterPrice(int dataLayer) {
			return 0;
		}

		@Override
		public double getStartBottomPrice(int dataLayer) {
			return 0;
		}

		@Override
		public long getEndTime(int dataLayer) {
			return 0;
		}

		@Override
		public double getEndTopPrice(int dataLayer) {
			return 0;
		}

		@Override
		public double getEndCenterPrice(int dataLayer) {
			return 0;
		}

		@Override
		public double getEndBottomPrice(int dataLayer) {
			return 0;
		}

		@Override
		public boolean isComputed(int dataLayer) {
			return false;
		}

		@Override
		public IChannel2Collection getChannel(int dataLayer) {
			return IChannel2Collection.EMPTY;
		}

	};

	public long getStartTime(int dataLayer);

	public double getStartTopPrice(int dataLayer);

	public double getStartCenterPrice(int dataLayer);

	public double getStartBottomPrice(int dataLayer);

	public long getEndTime(int dataLayer);

	public double getEndTopPrice(int dataLayer);

	public double getEndCenterPrice(int dataLayer);

	public double getEndBottomPrice(int dataLayer);

	public boolean isComputed(int dataLayer);

	public IChannel2Collection getChannel(int dataLayer);
}
