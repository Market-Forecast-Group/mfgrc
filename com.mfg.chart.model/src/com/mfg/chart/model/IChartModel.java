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

public interface IChartModel {
	IChartModel EMPTY = new EmptyChartModel();

	public ISyntheticModel getSyntheticModel();

	public int getDataLayerCount();

	public IPriceModel getPriceModel();

	public IScaledIndicatorModel getScaledIndicatorModel();

	public ITradingModel getTradingModel();

	// TODO: This should be moved to the execution model.
	public IPositionCollection getPendingOrdersModel();

	public boolean isAlive();

	public ITemporalPricesModel getTemporalPricesModel();

	public IDataLayerModel getDataLayerModel();

	public void setRangeModel(IDataLayerModel rangeModel);

	public long getToken();
}
