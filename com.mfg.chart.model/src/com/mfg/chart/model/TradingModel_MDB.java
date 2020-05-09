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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mfg.tradingdb.mdb.EquityMDB;
import com.mfg.tradingdb.mdb.TradeMDB;
import com.mfg.tradingdb.mdb.TradeMDB.Cursor;
import com.mfg.tradingdb.mdb.TradeMDB.RandomCursor;
import com.mfg.tradingdb.mdb.TradeMDB.Record;
import com.mfg.tradingdb.mdb.TradingMDBSession;

class EquityCollection extends ItemCollection<EquityMDB.Record> implements
		ITimePriceCollection {

	protected final long _lastTime;

	public EquityCollection(EquityMDB.Record[] data, long lastTime) {
		super(data);
		_lastTime = lastTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getTime(int)
	 */
	@Override
	public long getTime(int index) {
		if (index == 0) {
			return 0;
		}
		if (index == getSize() - 1) {
			return _lastTime;
		}
		return getItem(index - 1).fakeTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getPrice(int)
	 */
	@Override
	public double getPrice(int index) {
		if (index == 0) {
			return 0;
		}

		int i = index - 1;
		int lastIndex = getSize() - 3;

		if (i >= lastIndex) {
			i = lastIndex;
		}

		return getItem(i).total;
	}

	@Override
	public int getSize() {
		int size = super.getSize();
		return size == 0 ? 0 : size + 2;
	}

}

class EquityIndexCollection extends ItemCollection<EquityMDB.Record> implements
		ITimePriceCollection {

	public EquityIndexCollection(EquityMDB.Record[] data) {
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getTime(int)
	 */
	@Override
	public long getTime(int index) {
		return getItem(index).index;
	}

	/*
	 * ex (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getPrice(int)
	 */
	@Override
	public double getPrice(int index) {
		return getItem(index).total;
	}
}

/**
 * 
 * @author arian
 * 
 */
public class TradingModel_MDB extends Model_MDB implements ITradingModel {

	TradeMDB _tradeMdb;
	protected EquityMDB _equityMdb;
	private IPriceModel _priceModel;
	private final IProbabilityModel[] _probModelMap;
	private final IProbabilityModel[] _probPercentCurrentModelMap;
	private final IProbabilityModel[] _probPercentTHModelMap;
	private final boolean _percentProbabilityMode;
	private final IHSProbsModel[] _hsProbModels;
	private final boolean _conditionalProbabilitiesOnly;
	private int _scaleCount;
	protected boolean _equityShowIndex;

	/**
	 * @param session
	 * @throws IOException
	 */
	public TradingModel_MDB(TradingMDBSession session, IPriceModel priceModel) {
		super(session);
		try {
			_tradeMdb = session.connectTo_TradeMDB();
			_equityMdb = session.connectTo_EquityMDB();
			this._priceModel = priceModel;

			this._scaleCount = session.getScalesCount();
			_percentProbabilityMode = session.isPercentProbabilityMode();
			_conditionalProbabilitiesOnly = session
					.isConditionalProbabilitiesOnly();
			_probModelMap = new ProbabilityModel_MDB[_scaleCount + 1];
			_probPercentCurrentModelMap = new ProbabilityPercentModel_MDB[_scaleCount + 1];
			_probPercentTHModelMap = new ProbabilityPercentModel_MDB[_scaleCount + 1];
			_hsProbModels = new IHSProbsModel[_scaleCount + 1];

			int firstScale = 2; // magic number;
			for (int level = firstScale; level <= _scaleCount; level++) {
				_probModelMap[level] = new ProbabilityModel_MDB(session, level);
				_probPercentCurrentModelMap[level] = new ProbabilityPercentModel_MDB(
						session, level, false);
				if (!_conditionalProbabilitiesOnly) {
					_probPercentTHModelMap[level] = new ProbabilityPercentModel_MDB(
							session, level, true);
				}
				_hsProbModels[level] = IHSProbsModel.EMPTY;
			}

			_equityShowIndex = true;

		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public boolean isEquityShowIndex() {
		return _equityShowIndex;
	}

	@Override
	public void setEquityShowIndex(boolean equityShowIndex) {
		_equityShowIndex = equityShowIndex;
	}

	public TradingMDBSession getTradingSession() {
		return (TradingMDBSession) _session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IScaledIndicatorModel#getProbabilityModel2(int,
	 * boolean)
	 */
	@Override
	public IProbabilityModel getProbabilityModel(int level) {
		return _probModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.model.IScaledIndicatorModel#getProbabilityPercentCurrentModel
	 * (int)
	 */
	@Override
	public IProbabilityModel getProbabilityPercentCurrentModel(int level) {
		return _probPercentCurrentModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.model.IScaledIndicatorModel#getProbabilityPercentTHModel
	 * (int)
	 */
	@Override
	public IProbabilityModel getProbabilityPercentTHModel(int level) {
		return _probPercentTHModelMap[level];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IScaledIndicatorModel#getHSProbModel(int)
	 */
	@Override
	public IHSProbsModel getHSProbModel(int level) {
		return _hsProbModels[level];
	}

	/**
	 * Clear all the previous models because only one model will be dislayed.
	 * 
	 * @param level
	 * @param hsProbsModel
	 */
	public void setHSProbModel(int level, IHSProbsModel hsProbsModel) {
		for (int i = 0; i < _hsProbModels.length; i++) {
			_hsProbModels[i] = IHSProbsModel.EMPTY;
		}
		_hsProbModels[level] = hsProbsModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IScaledIndicatorModel#isPercentProbabilityMode()
	 */
	@Override
	public boolean isPercentProbabilityMode() {
		return _percentProbabilityMode;
	}

	/**
	 * @return the conditionalProbabilitiesOnly
	 */
	@Override
	public boolean isConditionalProbabilitiesOnly() {
		return _conditionalProbabilitiesOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getOpenPositionCount(long, long)
	 */
	@Override
	public int getOpenPositionCount(long lowerTime, long upperTime) {
		try {
			if (_tradeMdb.size() == 0) {
				return 0;
			}
			RandomCursor c = _tradeMdb.thread_randomCursor();
			long start = _tradeMdb.indexOfOpenTime(c, lowerTime);
			long end = _tradeMdb.indexOfOpenTime(c, upperTime);

			return (int) (end - start);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getTrade(long, long)
	 */
	@Override
	public ITradeCollection getTrade(long lowerTime, long upperTime,
			boolean includeClosedTrades) {
		try {
			RandomCursor c1 = _tradeMdb.thread_randomCursor();
			long start1 = _tradeMdb.indexOfOpenTime(c1, lowerTime);
			long start2 = _tradeMdb.indexOfCloseTime(c1, lowerTime);
			long stop1 = _tradeMdb.indexOfOpenTime(c1, upperTime);
			long stop2 = _tradeMdb.indexOfCloseTime(c1, upperTime);

			long start = Math.min(start1, start2);
			long stop = Math.max(stop1, stop2);

			Cursor c2 = _tradeMdb.thread_cursor();
			Record[] data = _tradeMdb.select(c2, start, stop);

			List<Record> list = new ArrayList<>();
			for (Record r : data) {
				if (includeClosedTrades || !includeClosedTrades && !r.isClosed) {
					list.add(r);
				}
			}

			return new TradeCollection(list.toArray(new Record[list.size()]));
		} catch (IOException e) {
			throw new ChartModelException(e);
		}

	}

	@Override
	public IOpeningHandler getOpeningHandler(double time, double price,
			double xspace, double yspace) {
		try {
			if (_tradeMdb.size() > 0) {
				RandomCursor c = _tradeMdb.thread_randomCursor();
				final long idx = _tradeMdb.indexOfOpenTime(c, (long) time);
				final Record r = _tradeMdb.record(c, idx);
				long xdiff = (long) Math.abs(r.openTime - time);
				if (!r.isClosed && xdiff < xspace) {
					long diff0 = (long) Math.abs(r.opening0 - price);
					long diff1 = (long) Math.abs(r.opening1 - price);
					if (diff0 < diff1 && diff0 < xspace) {
						return new IOpeningHandler() {
							@Override
							public int getOrderId() {
								return r.opening0_orderId;
							}

							@Override
							public long getTime() {
								return r.openTime;
							}

							@Override
							public long getPrice() {
								return r.opening0;
							}

							@Override
							public boolean modifyPrice(long aPrice) {
								try {
									r.opening0 = aPrice;
									_tradeMdb.replace(idx, r);
									return true;
								} catch (IOException e) {
									throw new ChartModelException(e);
								}
							}
						};
					} else if (diff1 < xspace) {
						return new IOpeningHandler() {

							@Override
							public int getOrderId() {
								return r.opening1_orderId;
							}

							@Override
							public long getTime() {
								return r.openTime;
							}

							@Override
							public long getPrice() {
								return r.opening1;
							}

							@Override
							public boolean modifyPrice(long aPrice) {
								try {
									r.opening1 = aPrice;
									_tradeMdb.replace(idx, r);
									return true;
								} catch (IOException e) {
									throw new ChartModelException(e);
								}
							}
						};

					}
				}
			}
			return null;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public String getStopLoss_TakeProfit_Tooltip(long x, long y,
			double zoomFactor) {
		try {
			String tooltip = null;
			if (_tradeMdb.size() > 0) {
				RandomCursor c = _tradeMdb.thread_randomCursor();
				long idx = _tradeMdb.indexOfOpenTime(c, x);
				Record r = _tradeMdb.record(c, idx);
				long xdiff = (long) (Math.abs(r.openTime - x) * zoomFactor);
				if (xdiff < 30) {
					long max = Math.abs(r.opening0 - r.opening1) / 4;
					long diff0 = Math.abs(r.opening0 - y);
					long diff1 = Math.abs(r.opening1 - y);
					if (diff0 < max || diff1 < max) {
						if (diff0 < diff1) {
							String type = r.opening0_childType == 0 ? "SL"
									: "TP";
							tooltip = type + " Time=" + r.openTime + ";" + type
									+ " Price = " + r.opening0;
						} else {
							String type = r.opening1_childType == 0 ? "SL"
									: "TP";
							tooltip = type + " Time=" + r.openTime + ";" + type
									+ " Price = " + r.opening1;
						}
					}
				}
			}
			return tooltip;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public ITimePriceCollection getEquity(long lowerTime, long upperTime) {
		EquityMDB.Record[] data;
		try {
			if (_equityShowIndex) {
				long start = lowerTime < 0 ? 0 : lowerTime;
				long size = _equityMdb.size();
				long stop = upperTime > size ? size : upperTime;
				EquityMDB.RandomCursor c1 = _equityMdb.thread_randomCursor();
				EquityMDB.Cursor c2 = _equityMdb.thread_cursor();
				data = _equityMdb.select_sparse(c1, c2, start, stop,
						maxNumberOfPointsToShow);
				return new EquityIndexCollection(data);
			}
			EquityMDB.RandomCursor c1 = _equityMdb.thread_randomCursor();
			EquityMDB.Cursor c2 = _equityMdb.thread_cursor();
			data = _equityMdb.select_sparse__where_FakeTime_in(c1, c2,
					lowerTime, upperTime, maxNumberOfPointsToShow);
			return new EquityCollection(data, getEquityUpperTime());
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public double getEquityCloseTotal(long time) {
		try {
			if (_equityMdb.size() > 0) {
				EquityMDB.RandomCursor c = _equityMdb.thread_randomCursor();
				if (_equityShowIndex) {
					c.seek(time);
				} else {
					long i = _equityMdb.indexOfFakeTime(c, time);
					c.seek(i);
				}
				return c.total;
			}
			return 0;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getEquityLowerTime()
	 */
	@Override
	public double getEquityLowerTime() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getLastEquityTime()
	 */
	@Override
	public long getEquityUpperTime() {
		if (_equityShowIndex) {
			try {
				long size = _equityMdb.size();
				return size == 0 ? 0 : size - 1;
			} catch (IOException e) {
				throw new ChartModelException(e);
			}
		}
		return _priceModel.getUpperDisplayTime(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getEquityTooltip(double)
	 */
	@Override
	public String getEquityTooltip(double aXvalue) {
		String tip;
		try {
			if (_equityMdb.size() > 0) {
				long i;
				EquityMDB.RandomCursor c = _equityMdb.thread_randomCursor();
				if (_equityShowIndex) {
					i = (long) aXvalue;
				} else {
					i = _equityMdb.indexOfFakeTime(c, (long) aXvalue);
				}
				c.seek(i);
				tip = "Money=" + c.total + ", Price=" + c.totalPrice;
				return tip;
			}
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getEquityRealTime(long)
	 */
	@Override
	public long getEquityRealTime(int dataLayer, long x) {
		try {
			EquityMDB.Record r1;
			EquityMDB.RandomCursor c = _equityMdb.thread_randomCursor();
			if (_equityShowIndex) {
				r1 = _equityMdb.record(c, x);
			} else {
				r1 = _equityMdb.findRecord_where_fakeTime_is(c, x);
			}
			if (r1 != null) {
				return _priceModel.getPhysicalTime_from_FakeTime(dataLayer,
						r1.fakeTime);
			}
			return 0;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IExecutionModel#getEquityFakeTime(long)
	 */
	@Override
	public long getEquityFakeTime(long index) {
		try {
			if (_equityShowIndex) {
				return _equityMdb.size() == 0 ? 0 : index;
			}
			EquityMDB.RandomCursor c = _equityMdb.thread_randomCursor();
			EquityMDB.Record r = _equityMdb.findRecord_where_fakeTime_is(c,
					index);
			return r == null ? 0 : r.fakeTime;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}
}
