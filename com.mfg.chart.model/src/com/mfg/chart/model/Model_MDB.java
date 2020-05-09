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

import com.mfg.inputdb.prices.CommonMDBSession;

/**
 * @author arian
 * 
 */
public abstract class Model_MDB {
	protected static int maxNumberOfPointsToShow = 300;

	/**
	 * @param theMaxNumberOfPointsToShow
	 *            the maxNumberOfPointsToShow to set
	 */
	public static void setMaxNumberOfPointsToShow(int theMaxNumberOfPointsToShow) {
		Model_MDB.maxNumberOfPointsToShow = theMaxNumberOfPointsToShow;
	}

	public static int getMaxNumberOfPointsToShow() {
		return maxNumberOfPointsToShow;
	}

	protected final CommonMDBSession _session;
	private IChartModel _chartModel;

	public Model_MDB(CommonMDBSession session) {
		this._session = session;
	}

	/**
	 * @return the chartModel
	 */
	public IChartModel getChartModel() {
		return _chartModel;
	}

	protected long getLowerDisplayTime(int dataLayer) {
		return _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
	}

	/**
	 * @param chartModel
	 *            the chartModel to set
	 */
	public void setChartModel(IChartModel chartModel) {
		this._chartModel = chartModel;
	}
}
