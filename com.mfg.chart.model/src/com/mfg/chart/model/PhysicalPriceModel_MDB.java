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

import java.io.IOException;

import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.Cursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;

public class PhysicalPriceModel_MDB extends PriceModel_MDB {

	private class PhysicalPriceCollection extends ItemCollection<Record>
			implements IPriceCollection {

		private final int _dataLayer;

		public PhysicalPriceCollection(int dataLayer, Record[] data) {
			super(data, 0, data.length);
			_dataLayer = dataLayer;
		}

		@Override
		public long getTime(int index) {
			return getDisplayTime(_dataLayer, getItem(index));
		}

		@Override
		public double getPrice(int index) {
			return getItem(index).price;
		}

		@Override
		public boolean isReal(int index) {
			return getItem(index).real;
		}
	}

	public PhysicalPriceModel_MDB(PriceMDBSession session) {
		super(session);
	}

	long getDisplayTime(int dataLayer, Record record) {
		return record.physicalTime - getLowerPhysicalTime(dataLayer);
	}

	@Override
	public double getTickSize() {
		return priceSession.getTickSize();
	}

	@Override
	public int getTickScale() {
		return priceSession.getTickScale();
	}

	@Override
	public IPriceCollection getVolumes(final int dataLayer, long aLowerTime,
			long aUpperTime, long lowerPrice, long upperPrice) {
		try {
			Record[] data = getPricesData(dataLayer, aLowerTime, aUpperTime,
					getMaxNumberOfPointsToShow());
			return new VolumeCollection(data, aLowerTime, aUpperTime,
					lowerPrice, upperPrice) {
				@Override
				public long getTime(int index) {
					return getDisplayTime(dataLayer, getItem(index));
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
			throw new ChartModelException(e);
		}
	}

	@Override
	public IPriceCollection getPrices(int dataLayer, long rangeLower,
			long rangeUpper, int maxNumberOfPoints) {
		try {
			Record[] data = getPricesData(dataLayer, rangeLower, rangeUpper,
					maxNumberOfPoints);
			return new PhysicalPriceCollection(dataLayer, data);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	private Record[] getPricesData(int dataLayer, long rangeLower,
			long rangeUpper, int maxNumberOfPoints) throws IOException {
		long lowerDisplayTime = getLowerDisplayTime(dataLayer);
		long lowerTime = rangeLower + lowerDisplayTime;
		long upperTime = rangeUpper + lowerDisplayTime;

		Record[] data;

		PriceMDB mdb = getMDB(dataLayer);
		RandomCursor c1 = mdb.thread_randomCursor();
		if (mdb.size() == 0) {
			data = mdb.NO_DATA;
		} else {
			long start = mdb.indexOfPhysicalTime(c1, lowerTime) - 1;
			long stop = mdb.indexOfPhysicalTime(c1, upperTime) + 1;
			Cursor c2 = mdb.thread_cursor();
			data = mdb.select_sparse(c1, c2, start, stop, maxNumberOfPoints);
		}
		return data;
	}

	@Override
	public LayerProjection getLayerProjection(int fromLayer,
			long aFromLowerTime, long aFromUpperTime, int toLayer) {
		try {
			PriceMDB fromMdb = mdbList[fromLayer];
			PriceMDB toMdb = mdbList[toLayer];

			if (fromMdb.size() == 0 || toMdb.size() == 0) {
				return null;
			}

			long lowerTime = getLowerDisplayTime(fromLayer);
			long fromLowerTime = aFromLowerTime + lowerTime;
			long fromUpperTime = aFromUpperTime + lowerTime;

			long toLowerDate = fromLowerTime;
			long toUpperDate = fromUpperTime;
			RandomCursor c = toMdb.thread_randomCursor();
			long firstToDate = toMdb.first(c).physicalTime;
			long lastToDate = toMdb.last(c).physicalTime;

			boolean lowestTime = firstToDate > toLowerDate;
			boolean bigestTime = lastToDate < toUpperDate;

			boolean offData = toUpperDate < firstToDate
					|| toLowerDate > lastToDate;

			// convert to the to-mdb times
			lowerTime = toMdb.first(c).physicalTime;

			toLowerDate = Math.max(firstToDate, toLowerDate) - lowerTime;
			toUpperDate = Math.min(lastToDate, toUpperDate) - lowerTime;

			return new LayerProjection(toLowerDate, toUpperDate, toLowerDate,
					toUpperDate, lowestTime, bigestTime, offData);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public LayerProjection getLayerProjection(int fromLayer,
			LayerProjection fromProjection, int toLayer) {
		try {
			PriceMDB fromMdb = mdbList[fromLayer];
			PriceMDB toMdb = mdbList[toLayer];

			if (fromMdb.size() == 0 || toMdb.size() == 0) {
				return null;
			}

			long lowerTime = getLowerDisplayTime(fromLayer);
			long fromLowerTime = fromProjection.getLowerDisplayTime()
					+ lowerTime;
			long fromUpperTime = fromProjection.getUpperDisplayTime()
					+ lowerTime;

			long toLowerDate = fromLowerTime;
			long toUpperDate = fromUpperTime;
			RandomCursor c = toMdb.thread_randomCursor();
			long firstToDate = toMdb.first(c).physicalTime;
			long lastToDate = toMdb.last(c).physicalTime;

			boolean lowestTime = firstToDate > toLowerDate;
			boolean bigestTime = lastToDate < toUpperDate;

			boolean offData = toUpperDate < firstToDate
					|| toLowerDate > lastToDate;

			// convert to the to-mdb times
			lowerTime = toMdb.first(c).physicalTime;

			toLowerDate = Math.max(firstToDate, toLowerDate) - lowerTime;
			toUpperDate = Math.min(lastToDate, toUpperDate) - lowerTime;

			return new LayerProjection(toLowerDate, toUpperDate, toLowerDate,
					toUpperDate, lowestTime, bigestTime, offData);

		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getDisplayTime_from_PhysicalTime(int dataLayer,
			long physicalTime) {
		return physicalTime - getLowerPhysicalTime(dataLayer);
	}

	@Override
	public long getLowerDisplayTime(int dataLayer) {
		try {
			PriceMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0)
				return 0;
			RandomCursor c = mdb.thread_randomCursor();
			c.seekFirst();
			return c.physicalTime;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/**
	 * @deprecated Use {@link #getDataLayerUpperDisplayTime(int)}
	 */
	@Deprecated
	@Override
	public long getUpperDisplayTime(int dataLayer) {
		return getDataLayerUpperDisplayTime(dataLayer);
	}

	@Override
	public long getDataLayerLowerDisplayTime(int dataLayer) {
		return 0;
	}

	@Override
	public long getDataLayerUpperDisplayTime(int dataLayer) {
		try {
			PriceMDB mdb = mdbList[dataLayer];
			if (mdb.size() == 0)
				return 0;

			RandomCursor c = mdb.thread_randomCursor();
			c.seekLast();
			return c.physicalTime - getLowerDisplayTime(dataLayer);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getPricesDistance(int dataLayer, long lower, long upper) {
		PriceMDB mdb = mdbList[dataLayer];
		try {
			long lowerTime = getLowerDisplayTime(dataLayer);

			long start = lower + lowerTime;
			long stop = upper + lowerTime;

			RandomCursor c = mdb.thread_randomCursor();
			return mdb.countPhysicalTime(c, start, stop);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getDisplayTimeOffset(int dataLayer, long displayTime,
			long distance) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			RandomCursor c = mdb.thread_randomCursor();

			long lower = getLowerPhysicalTime(dataLayer);
			long date = lower + displayTime;
			long i1 = mdb.indexOfPhysicalTime(c, date);
			long i2 = i1 + distance;
			i2 = i2 < 0 ? 0 : i2;
			long size = mdb.size();
			i2 = i2 >= size ? size - 1 : i2;
			c.seek(i2);
			return c.physicalTime - lower;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public ITimesOfTheDayCollection getTimeOfTheDayCollection(
			final int dataLayer, int hh, int mm, int maxNumOfDays,
			long lowerFakeTime, long upperFakeTime) {
		long lowerTime = getLowerDisplayTime(dataLayer);
		long lower = lowerFakeTime + lowerTime;
		long upper = upperFakeTime + lowerTime;
		PriceMDB mdb = getMDB(dataLayer);
		try {
			RandomCursor c = mdb.thread_randomCursor();
			long start = mdb.indexOfPhysicalTime(c, lower);
			long stop = mdb.indexOfPhysicalTime(c, upper);
			final ITimesOfTheDayCollection col = PriceModel_MDB
					.getTimeOfTheDayCollection(mdb, hh, mm, maxNumOfDays,
							start, stop, false);

			return new ITimesOfTheDayCollection() {

				@Override
				public long getTime(int index) {
					return col.getTime(index) - getLowerDisplayTime(dataLayer);
				}

				@Override
				public int getSize() {
					return col.getSize();
				}

				@Override
				public String getLabel(int index) {
					return col.getLabel(index);
				}
			};
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getLowerDisplayTime_from_DisplayTime(int dataLayer,
			long rangeLower) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			if (mdb.size() == 0)
				return 0;

			RandomCursor first = mdb.thread_randomCursor();
			first.seekFirst();
			return Math.max(rangeLower, first.physicalTime
					- getLowerDisplayTime(dataLayer));
		} catch (IOException e) {
			throw new ChartModelException(e);
		}

	}

	@Override
	public long getUpperDisplayTime_from_DisplayTime(int dataLayer,
			long rangeUpper) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			if (mdb.size() == 0)
				return 0;

			RandomCursor last = mdb.thread_randomCursor();
			last.seekLast();
			return Math.min(rangeUpper, last.physicalTime
					- getLowerDisplayTime(dataLayer));
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getPhysicalTime_from_DisplayTime(int layer, long displayTime) {
		PriceMDB mdb = mdbList[layer];
		try {
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor first = mdb.thread_randomCursor();
			first.seekFirst();
			long firstTime = first.physicalTime;
			return firstTime + displayTime;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getFakeTime_from_DisplayTime(int dataLayer, long displayTime) {
		return getFakeTime_from_PhysicalTime(dataLayer,
				getLowerPhysicalTime(dataLayer) + displayTime);
	}

	@Override
	public long getLowerPhysicalTime_from_DisplayTime(int dataLayer,
			long rangeLower) {
		return getPhysicalTime_from_DisplayTime(dataLayer, rangeLower);
	}

	@Override
	public long getUpperPhysicalTime_from_DisplayTime(int dataLayer,
			long rangeUpper) {
		return getPhysicalTime_from_DisplayTime(dataLayer, rangeUpper);
	}

	@Override
	public Long getStartRealtime(int dataLayer) {
		Long idx = super.getStartRealtime(dataLayer);
		Long time = null;
		if (idx != null) {
			time = Long.valueOf(super.getPhysicalTime_from_DisplayTime(
					dataLayer, idx.longValue())
					- getLowerDisplayTime(dataLayer));
		}
		return time;
	}

	@Override
	public long getLastTime(int dataLayer) {
		try {
			PriceMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c0 = mdb.thread_randomCursor();
			c0.seekLast();
			long start = c0.physicalTime;
			c0.seekLast();
			return c0.physicalTime - start;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}
}
