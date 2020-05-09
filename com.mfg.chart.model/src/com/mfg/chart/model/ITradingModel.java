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
public interface ITradingModel {

	public interface IOpeningHandler {
		public long getTime();

		public long getPrice();

		public boolean modifyPrice(long price);

		public int getOrderId();
	}

	public ITradingModel EMPTY = new ITradingModel() {

		@Override
		public int getOpenPositionCount(long lowerTime, long upperTime) {
			return 0;
		}

		@Override
		public ITradeCollection getTrade(long lowerTime, long upperTime,
				boolean includeClosed) {
			return ITradeCollection.EMPTY;
		}

		@Override
		public ITimePriceCollection getEquity(long lowerTime, long upperTime) {
			return ITimePriceCollection.EMPTY;
		}

		@Override
		public long getEquityUpperTime() {
			return 0;
		}

		@Override
		public double getEquityLowerTime() {
			return 0;
		}

		@Override
		public String getEquityTooltip(double xvalue) {
			return "";
		}

		@Override
		public long getEquityRealTime(int dataLayer, long fakeTime) {
			return 0;
		}

		@Override
		public long getEquityFakeTime(long index) {
			return 0;
		}

		@Override
		public IProbabilityModel getProbabilityModel(int level) {
			return IProbabilityModel.EMPTY;
		}

		@Override
		public IProbabilityModel getProbabilityPercentCurrentModel(int level) {
			return IProbabilityModel.EMPTY;
		}

		@Override
		public IProbabilityModel getProbabilityPercentTHModel(int level) {
			return IProbabilityModel.EMPTY;
		}

		@Override
		public boolean isPercentProbabilityMode() {
			return false;
		}

		@Override
		public boolean isConditionalProbabilitiesOnly() {
			return false;
		}

		@Override
		public IHSProbsModel getHSProbModel(int level) {
			return IHSProbsModel.EMPTY;
		}

		@Override
		public String getStopLoss_TakeProfit_Tooltip(long x, long y,
				double zoomFactor) {
			return null;
		}

		@Override
		public IOpeningHandler getOpeningHandler(double time, double price,
				double xspace, double yspace) {
			return null;
		}

		@Override
		public double getEquityCloseTotal(long time) {
			return 0;
		}

		@Override
		public boolean isEquityShowIndex() {
			return false;
		}

		@Override
		public void setEquityShowIndex(boolean equityShowIndex) {
			// nothing
		}
	};

	public int getOpenPositionCount(long lowerTime, long upperTime);

	public ITradeCollection getTrade(long lowerTime, long upperTime,
			boolean includeClosedTrades);

	public ITimePriceCollection getEquity(long lowerTime, long upperTime);

	public long getEquityUpperTime();

	public double getEquityLowerTime();

	public String getEquityTooltip(double xvalue);

	public long getEquityRealTime(int dataLayer, long index);

	public long getEquityFakeTime(long index);

	public IProbabilityModel getProbabilityModel(int level);

	public IProbabilityModel getProbabilityPercentCurrentModel(int level);

	public IProbabilityModel getProbabilityPercentTHModel(int level);

	public boolean isPercentProbabilityMode();

	public boolean isConditionalProbabilitiesOnly();

	public IHSProbsModel getHSProbModel(int level);

	public String getStopLoss_TakeProfit_Tooltip(long x, long y,
			double zoomFactor);

	public IOpeningHandler getOpeningHandler(double time, double price,
			double xspace, double yspace);

	public double getEquityCloseTotal(long time);

	public boolean isEquityShowIndex();

	public void setEquityShowIndex(boolean equityShowIndex);
}
