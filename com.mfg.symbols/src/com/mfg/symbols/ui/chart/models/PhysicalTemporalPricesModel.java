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
package com.mfg.symbols.ui.chart.models;

import java.awt.geom.Point2D;

import com.mfg.common.QueueTick;

/**
 * @author arian
 * 
 */
public class PhysicalTemporalPricesModel extends TemporalPricesModel {

	public PhysicalTemporalPricesModel(int layerCount) {
		super(layerCount);
	}

	@Override
	public Point2D getTempTick(int dataLayer) {
		Point2D point = super.getTempTick(dataLayer);
		if (point != null) {
			long lower = getLower(dataLayer);
			QueueTick tempTick = _tempTick[dataLayer];
			if (tempTick != null) {
				long t = tempTick.getPhysicalTime() - lower;
				point.setLocation(t, point.getY());
			}
		}
		return point;
	}

	private long getLower(int dataLayer) {
		return getChart().getModel().getPriceModel()
				.getLowerDisplayTime(dataLayer);
	}

	@Override
	public Point2D getLastFinalTick(int dataLayer) {
		Point2D point = super.getLastFinalTick(dataLayer);
		if (point != null) {
			long lower = getLower(dataLayer);
			long t = _lastFinalTick[dataLayer].getPhysicalTime() - lower;
			point.setLocation(t, point.getY());
		}
		return point;
	}
}
