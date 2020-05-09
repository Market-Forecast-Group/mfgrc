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

import java.awt.geom.Point2D;

/**
 * @author arian
 * 
 */
public interface ITemporalPricesModel {
	ITemporalPricesModel EMPTY = new ITemporalPricesModel() {

		@Override
		public Point2D getTempTick(int dataLayer) {
			return null;
		}

		@Override
		public Point2D getLastFinalTick(int dataLayer) {
			return null;
		}

		@Override
		public int getModificationToken() {
			return 0;
		}

	};

	public Point2D getTempTick(int dataLayer);

	public Point2D getLastFinalTick(int dataLayer);

	public int getModificationToken();
}
