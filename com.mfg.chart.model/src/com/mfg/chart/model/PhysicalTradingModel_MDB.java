package com.mfg.chart.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mfg.tradingdb.mdb.EquityMDB;
import com.mfg.tradingdb.mdb.EquityMDB.Record;
import com.mfg.tradingdb.mdb.TradeMDB;
import com.mfg.tradingdb.mdb.TradeMDB.Cursor;
import com.mfg.tradingdb.mdb.TradeMDB.RandomCursor;
import com.mfg.tradingdb.mdb.TradingMDBSession;
import com.mfg.utils.collections.TimeMap;

public class PhysicalTradingModel_MDB extends TradingModel_MDB {

	private TimeMap _timeMap;

	public PhysicalTradingModel_MDB(TimeMap timeMap, TradingMDBSession session,
			IPriceModel priceModel) {
		super(session, priceModel);
		_equityShowIndex = false;
		_timeMap = timeMap;
	}

	class PhysicalEquityCollection extends EquityCollection {

		public PhysicalEquityCollection(Record[] data, long lastTime) {
			super(data, lastTime);
		}

		@Override
		public long getTime(int index) {
			if (index == 0) {
				return 0;
			}
			if (index == getSize() - 1) {
				return _lastTime;
			}
			return getItem(index - 1).physicalTime - getLowerDisplayTime(0);
		}
	}

	@Override
	public ITimePriceCollection getEquity(long aLowerTime, long aUpperTime) {
		EquityMDB.Record[] data;
		try {
			long lowerTime = aLowerTime + getLowerDisplayTime(0);
			long upperTime = aUpperTime + getLowerDisplayTime(0);

			com.mfg.tradingdb.mdb.EquityMDB.RandomCursor c1 = _equityMdb
					.thread_randomCursor();
			com.mfg.tradingdb.mdb.EquityMDB.Cursor c2 = _equityMdb
					.thread_cursor();

			data = _equityMdb.select_sparse__where_PhysicalTime_in(c1, c2,
					lowerTime, upperTime, maxNumberOfPointsToShow);

			return new PhysicalEquityCollection(data, getEquityUpperTime());
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getEquityRealTime(int dataLayer, long x) {
		return getLowerDisplayTime(0) + x;
	}

	@Override
	public double getEquityCloseTotal(long time) {
		int fakeTime = _timeMap.indexOf(getLowerDisplayTime(0) + time);
		return super.getEquityCloseTotal(fakeTime);
	}

	@Override
	public String getEquityTooltip(double aXvalue) {
		try {
			if (_equityMdb.size() > 0) {
				long physicalTime = (long) (getLowerDisplayTime(0) + aXvalue);
				com.mfg.tradingdb.mdb.EquityMDB.RandomCursor c = _equityMdb
						.thread_randomCursor();
				long i = _equityMdb.indexOfPhysicalTime(c, physicalTime);
				c.seek(i);
				return "Money=" + c.total + ", Price=" + c.totalPrice;
			}
			return "";
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	class PhysicalTradeCollection extends TradeCollection {

		private long _lower;

		public PhysicalTradeCollection(TradeMDB.Record[] data, long lower) {
			super(data);
			_lower = lower;
		}

		@Override
		public long getCloseTime(int index) {
			return getItem(index).closePhysicalTime - _lower;
		}

		@Override
		public long getOpenTime(int index) {
			return getItem(index).openPhysicalTime - _lower;
		}

	}

	@Override
	public ITradeCollection getTrade(long aLowerTime, long aUpperTime,
			boolean includeClosedTrades) {

		try {

			long lowerDisplayTime = getLowerDisplayTime(0);
			long lowerTime = lowerDisplayTime + aLowerTime;
			long upperTime = lowerDisplayTime + aUpperTime;

			RandomCursor c1 = _tradeMdb.thread_randomCursor();
			Cursor c2 = _tradeMdb.thread_cursor();

			long start1 = _tradeMdb.indexOfOpenPhysicalTime(c1, lowerTime);
			long start2 = _tradeMdb.indexOfClosePhysicalTime(c1, lowerTime);
			long stop1 = _tradeMdb.indexOfOpenPhysicalTime(c1, upperTime);
			long stop2 = _tradeMdb.indexOfClosePhysicalTime(c1, upperTime);

			long start = Math.min(start1, start2);
			long stop = Math.max(stop1, stop2);

			TradeMDB.Record[] data = _tradeMdb.select(c2, start, stop);

			List<TradeMDB.Record> list = new ArrayList<>();
			for (TradeMDB.Record r : data) {
				if (includeClosedTrades || !includeClosedTrades && !r.isClosed) {
					list.add(r);
				}
			}

			return new PhysicalTradeCollection(
					list.toArray(new TradeMDB.Record[list.size()]),
					lowerDisplayTime);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}
}
