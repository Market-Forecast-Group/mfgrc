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
public abstract class BaseChartModel implements IChartModel {
	private ITradingModel executionModel;

	public BaseChartModel() {
		executionModel = ITradingModel.EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.models.IChartModel#getExecutionModel()
	 */
	@Override
	public ITradingModel getTradingModel() {
		return executionModel;
	}

	/**
	 * @param executionModel1
	 *            the executionModel to set
	 */
	public void setExecutionModel(ITradingModel executionModel1) {
		this.executionModel = executionModel1;
	}
}
