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

public interface IScaledIndicatorModel {
	public IScaledIndicatorModel EMPTY = new IScaledIndicatorModel() {

		@Override
		public int getScaleCount() {
			return 0;
		}

		@Override
		public IPivotModel getPivotModel(int level) {
			return null;
		}

		@Override
		public IChannelModel getChannelModel(int level) {
			return null;
		}

		@Override
		public IBandsModel getBandsModel(int level) {
			return null;
		}

		@Override
		public IRealTimeZZModel getRealTimeZZModel(int level) {
			return null;
		}

		@Override
		public IParallelRealTimeZZModel getParallelRealTimeZZModel(int level) {
			return IParallelRealTimeZZModel.EMPTY;
		}

		@Override
		public void setRealTimeChannelModel(int level,
				IRealTimeChannelModel model) {
			//
		}

		@Override
		public void setRealTimeZZModel(int level, IRealTimeZZModel model) {
			//
		}

		@Override
		public void setParallelRealTimeZZModel(int level,
				IParallelRealTimeZZModel model) {
			//
		}

		@Override
		public IRealTimeChannelModel getRealTimeChannelModel(int level) {
			return null;
		}

		@Override
		public IAutoTimeLinesModel getAutoTimeLinesModel(int level) {
			return null;
		}

		@Override
		public int getFirstScale() {
			return 2;
		}

		@Override
		public ITrendLinesModel getTrendLinesModel() {
			return ITrendLinesModel.EMPTY;
		}

	};

	public int getScaleCount();

	public IAutoTimeLinesModel getAutoTimeLinesModel(int level);

	public IBandsModel getBandsModel(int level);

	public IPivotModel getPivotModel(int level);

	public IRealTimeZZModel getRealTimeZZModel(int level);

	public IParallelRealTimeZZModel getParallelRealTimeZZModel(int level);

	public IRealTimeChannelModel getRealTimeChannelModel(int level);

	public void setRealTimeChannelModel(int level, IRealTimeChannelModel model);

	public void setRealTimeZZModel(int level, IRealTimeZZModel model);

	public void setParallelRealTimeZZModel(int level,
			IParallelRealTimeZZModel model);

	public IChannelModel getChannelModel(int level);

	public int getFirstScale();

	/**
	 * @return
	 */
	ITrendLinesModel getTrendLinesModel();
}
