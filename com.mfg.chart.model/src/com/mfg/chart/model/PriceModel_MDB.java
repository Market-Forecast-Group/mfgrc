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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.Cursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;

public class PriceModel_MDB extends Model_MDB implements IPriceModel {

	protected final PriceMDB[] mdbList;
	protected PriceMDBSession priceSession;
	private static final DateFormat format = DateFormat.getDateTimeInstance(
			DateFormat.SHORT, DateFormat.SHORT);

	private static class PriceCollection extends ItemCollection<Record>
			implements IPriceCollection {

		public PriceCollection(Record[] data, int start, int len) {
			super(data, start, len);
		}

		/**
		 * @param selectAll
		 */
		public PriceCollection(Record[] data) {
			this(data, 0, data.length);
		}

		@Override
		public long getTime(int index) {
			return getItem(index).time;
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

	public static class VolumeCollection extends PriceCollection {

		private double _lowerPrice;
		private double _lowerVol;
		private double _upperPrice;
		private double _priceLen;
		private double _volLen;

		public VolumeCollection(Record[] data, long lowerTime, long upperTime,
				long lowerPrice, long upperPrice) {
			super(data);
			computeRange(lowerTime, upperTime, lowerPrice, upperPrice);
		}

		public void computeRange(long lowerTime, long upperTime,
				long lowerPrice, long upperPrice) {
			_lowerPrice = lowerPrice;
			_upperPrice = upperPrice;
			_priceLen = _upperPrice - _lowerPrice;

			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;

			for (int i = 0; i < _data.length; i++) {
				long time = getTime(i);
				if (time >= lowerTime && time <= upperTime) {
					Record r = _data[i];
					int vol = r.volume;
					if (vol < min) {
						min = vol;
					}
					if (vol > max) {
						max = vol;
					}
				}
			}
			_lowerVol = min;
			_volLen = max - min;
		}

		@Override
		public double getPrice(int index) {
			Record r = getItem(index);

			double volPart = r.volume - _lowerVol;
			double ratio = volPart / _volLen;

			double margin = _priceLen * 0.1;

			double vol = _lowerPrice + margin + ratio
					* (_priceLen - margin * 2);

			return vol;
		}
	}

	public PriceModel_MDB(PriceMDBSession session) {
		super(session);
		priceSession = session;
		try {
			int layersCount = session.getDataLayersCount();
			mdbList = new PriceMDB[layersCount];
			for (int i = 0; i < mdbList.length; i++) {
				mdbList[i] = session.connectTo_PriceMDB(i);
			}
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
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
	public IPriceCollection getPrices(int dataLayer, long aLowerTime,
			long upperTime, int maxNumberOfPoints) {
		Record[] data = getPricesData(dataLayer, aLowerTime, upperTime,
				maxNumberOfPoints);
		return new PriceCollection(data);
	}

	@Override
	public int getVolume_from_FakeTime(int dataLayer, long fakeTime) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			long size = mdb.size();
			if (size == 0) {
				return 0;
			}
			long pos = fakeTime < 0 ? 0 : (fakeTime >= size ? size - 1
					: fakeTime);
			RandomCursor c1 = mdb.thread_randomCursor();
			c1.seek(pos);
			return c1.volume;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ChartModelException(e);
		}
	}

	@Override
	public IPriceCollection getVolumes(int dataLayer, long lowerTime,
			long upperTime, long lowerPrice, long upperPrice) {
		Record[] data = getPricesData(dataLayer, lowerTime, upperTime,
				getMaxNumberOfPointsToShow());
		return new VolumeCollection(data, lowerTime, upperTime, lowerPrice,
				upperPrice);
	}

	private Record[] getPricesData(int dataLayer, long aLowerTime,
			long upperTime, int maxNumberOfPoints) {
		try {
			long lowerTime = aLowerTime - 1 < 0 ? 0 : aLowerTime - 1;
			PriceMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return mdb.NO_DATA;
			}
			RandomCursor c1 = mdb.thread_randomCursor();
			Cursor c2 = mdb.thread_cursor();
			Record first = mdb.first(c1);
			Record last = mdb.last(c1);
			long start = Math.max(lowerTime, first.time) - 1;
			long stop = Math.min(upperTime, last.time) + 1;
			Record[] data = mdb.select_sparse(c1, c2, start, stop,
					maxNumberOfPoints);
			return data;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public LayerProjection getLayerProjection(int fromLayer,
			long fromLowerTime, long fromUpperTime, int toLayer) {
		try {
			PriceMDB fromMdb = mdbList[fromLayer];
			PriceMDB toMdb = mdbList[toLayer];

			if (fromMdb.size() == 0 || toMdb.size() == 0) {
				return null;
			}
			// find real times
			long fromLowerIndex = fromMdb.indexOfTime(fromLowerTime);
			long fromUpperIndex = fromMdb.indexOfTime(fromUpperTime);

			RandomCursor fc = fromMdb.thread_randomCursor();

			Record fromLowerRecord = fromMdb.record(fc, fromLowerIndex);
			Record fromUpperRecord = fromMdb.record(fc, fromUpperIndex);

			long fromLowerDate = fromLowerRecord.physicalTime;
			long fromUpperDate = fromUpperRecord.physicalTime;

			RandomCursor tc = toMdb.thread_randomCursor();

			long toLowerIndex = toMdb.indexOfPhysicalTime(tc, fromLowerDate);
			long toUpperIndex = toMdb.indexOfPhysicalTime(tc, fromUpperDate);

			long toLowerDate = toMdb.record(tc, toLowerIndex).physicalTime;
			long toUpperDate = toMdb.record(tc, toUpperIndex).physicalTime;

			boolean lowestTime = toLowerIndex == 0;
			boolean bigestTime = toUpperIndex == toMdb.size() - 1;

			boolean offData = toUpperDate < fromLowerDate
					|| toLowerDate > fromUpperDate;

			return new LayerProjection(toLowerDate, toUpperDate, toLowerIndex,
					toUpperIndex, lowestTime, bigestTime, offData);

		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public LayerProjection getLayerProjection(int fromLayer,
			LayerProjection fromProjection, int toLayer) {
		try {
			PriceMDB toMdb = mdbList[toLayer];

			if (toMdb.size() == 0) {
				return null;
			}
			RandomCursor c = toMdb.thread_randomCursor();
			long toLowerIndex = toMdb.indexOfPhysicalTime(c,
					fromProjection.getLowerDate());
			long toUpperIndex = toMdb.indexOfPhysicalTime(c,
					fromProjection.getUpperDate());

			long toLowerDate = toMdb.record(c, toLowerIndex).physicalTime;
			long toUpperDate = toMdb.record(c, toUpperIndex).physicalTime;

			boolean lowestTime = toLowerIndex == 0;
			boolean bigestTime = toUpperIndex == toMdb.size() - 1;

			boolean offData = toUpperDate < fromProjection.getLowerDate()
					|| toLowerDate > fromProjection.getUpperDate();

			return new LayerProjection(toLowerDate, toUpperDate, toLowerIndex,
					toUpperIndex, lowestTime, bigestTime, offData);

		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	/**
	 * @return
	 */
	public PriceMDB getMDB(int dataLayer) {
		return mdbList[dataLayer];
	}

	@Override
	public long getPhysicalTime_from_FakeTime(int dataLayer, long fakeTime) {
		try {
			PriceMDB mdb = getMDB(dataLayer);
			RandomCursor c = mdb.thread_randomCursor();

			long size = mdb.size();
			Record record;
			try {
				long lastIndex = size - 1;
				if (fakeTime >= lastIndex) {
					record = mdb.record(c, lastIndex);
				} else {
					record = mdb.record(c, fakeTime);
				}
			} catch (IndexOutOfBoundsException e) {
				record = null;
			}
			return record == null ? 0 : record.physicalTime;
		} catch (IOException e) {
			return 0;
		}
	}

	@Override
	public long getDisplayTime_from_PhysicalTime(int dataLayer,
			long physicalTime) {
		PriceMDB mdb = mdbList[dataLayer];
		long i;
		try {
			RandomCursor c = mdb.thread_randomCursor();
			i = mdb.indexOfPhysicalTime(c, physicalTime);
			return i;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getLowerDisplayTime(int dataLayer) {
		return 0;
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
		try {
			PriceMDB mdb = mdbList[dataLayer];
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			c.seekFirst();
			return c.time;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getDataLayerUpperDisplayTime(int dataLayer) {
		try {
			PriceMDB mdb = mdbList[dataLayer];
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			c.seekLast();
			return c.time;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public Integer getLastPrice(int dataLayer) {
		try {
			PriceMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return null;
			}
			RandomCursor c = mdb.thread_randomCursor();
			c.seekLast();
			return Integer.valueOf(c.price);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}
	
	@Override
	public long getLastTime(int dataLayer) {
		try {
			PriceMDB mdb = getMDB(dataLayer);
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			c.seekLast();
			return c.time;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getPricesDistance(int dataLayer, long lower, long upper) {
		PriceMDB mdb = mdbList[dataLayer];
		try {
			if (mdb.size() == 0) {
				return 0;
			}

			long idxLower = mdb.indexOfTime(lower);
			long idxUpper = mdb.indexOfTime(upper);

			RandomCursor c = mdb.thread_randomCursor();
			Record recLower = mdb.record(c, idxLower);
			Record recUpper = mdb.record(c, idxUpper);

			long len = recUpper.time - recLower.time;

			return len;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getDisplayTimeOffset(int dataLayer, long displayTime,
			long distance) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			long i1 = mdb.indexOfTime(displayTime);
			long i2 = i1 + distance;
			i2 = i2 < 0 ? 0 : i2;
			long size = mdb.size();
			i2 = i2 >= size ? size - 1 : i2;
			RandomCursor c = mdb.thread_randomCursor();
			c.seek(i2);
			return c.time;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public ITimesOfTheDayCollection getTimeOfTheDayCollection(int dataLayer,
			int hh, int mm, int maxNumOfDays, long lowerFakeTime,
			long upperFakeTime) {
		return getTimeOfTheDayCollection(getMDB(dataLayer), hh, mm,
				maxNumOfDays, lowerFakeTime, upperFakeTime, true);
	}

	public static ITimesOfTheDayCollection getTimeOfTheDayCollection(
			PriceMDB mdb, int hh, int mm, int maxNumOfDays, long lowerFakeTime,
			long upperFakeTime, boolean returnFakeTimes) {
		try {

			TimeOfTheDayList list = new TimeOfTheDayList();

			long size = mdb.size();
			if (size > 0 && maxNumOfDays > 0) {
				long daysInMillis = TimeUnit.DAYS.toMillis(maxNumOfDays);

				RandomCursor cur = mdb.thread_randomCursor();
				long lower = lowerFakeTime < 0 ? 0 : lowerFakeTime;
				long upper = upperFakeTime < size ? upperFakeTime : size - 1;
				if (lower > size - 1) {
					lower = size - 1;
				}
				if (upper < 0) {
					upper = 0;
				}
				Record r1 = mdb.record(cur, lower);
				Record r2 = mdb.record(cur, upper);

				if (r1 != null && r2 != null
						&& r2.physicalTime - r1.physicalTime <= daysInMillis) {

					long t = r1.physicalTime;
					GregorianCalendar date = new GregorianCalendar();
					date.setTimeInMillis(t);
					date.set(Calendar.HOUR_OF_DAY, hh);
					date.set(Calendar.MINUTE, mm);
					date.set(Calendar.SECOND, 0);

					while (date.getTimeInMillis() <= r2.physicalTime) {
						if (date.getTimeInMillis() >= r1.physicalTime) {
							long fakeTime = mdb.indexOfPhysicalTime(cur,
									date.getTimeInMillis(), lower, upper);

							Record r = mdb.record(cur, fakeTime);
							GregorianCalendar c2 = new GregorianCalendar();
							c2.setTimeInMillis(r.physicalTime);
							c2.set(Calendar.SECOND, 0);

							if (c2.before(date)) {
								// get the next
								Record next = mdb.record(cur, fakeTime + 1);
								if (next.time <= upper) {
									r = next;
								}
							}
							String label = (hh < 10 ? "0" : "") + hh + ":"
									+ (mm < 10 ? "0" : "") + mm + " ("
									+ format.format(new Date(r.physicalTime))
									+ ")";
							list.add(returnFakeTimes ? r.time : r.physicalTime,
									label);
							if (list.getSize() == maxNumOfDays) {
								break;
							}
						}
						date.add(Calendar.DAY_OF_WEEK, 1);
					}
				}
			}
			return list;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getLowerDisplayTime_from_DisplayTime(int dataLayer,
			long rangeLower) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor first = mdb.thread_randomCursor();
			first.seekFirst();
			return Math.max(rangeLower, first.time);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}

	}

	@Override
	public long getUpperDisplayTime_from_DisplayTime(int dataLayer,
			long rangeUpper) {
		PriceMDB mdb = getMDB(dataLayer);
		try {
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor last = mdb.thread_randomCursor();
			last.seekLast();
			return Math.min(rangeUpper, last.time);
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getPhysicalTime_from_DisplayTime(int layer, long time) {
		try {
			PriceMDB mdb = mdbList[layer];
			if (mdb.size() == 0) {
				return 0;
			}
			long idx = mdb.indexOfTime(time);
			RandomCursor c = mdb.thread_randomCursor();
			c.seek(idx);
			return c.physicalTime;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getFakeTime_from_PhysicalTime(int layer, long physicalTime) {
		try {
			PriceMDB mdb = mdbList[layer];
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			long idx = mdb.indexOfPhysicalTime(c, physicalTime);
			c.seek(idx);
			return c.time;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getFakeTime_from_DisplayTime(int layer, long displayTime) {
		return displayTime;
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
	public long getLowerPhysicalTime(int dataLayer) {
		PriceMDB mdb = mdbList[dataLayer];
		try {
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			c.seekFirst();
			return c.physicalTime;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getUpperPhysicalTime(int dataLayer) {
		PriceMDB mdb = mdbList[dataLayer];
		try {
			if (mdb.size() == 0) {
				return 0;
			}
			RandomCursor c = mdb.thread_randomCursor();
			c.seekLast();
			return c.physicalTime;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}
	}

	@Override
	public long getDataLayerPricesCount(int layer) {
		PriceMDB mdb = mdbList[layer];
		long count;
		try {
			count = mdb.size();
			return count;
		} catch (IOException e) {
			throw new ChartModelException(e);
		}

	}

	@Override
	public Long getStartRealtime(int dataLayer) {
		Long[] times = priceSession.getStartRealtimes();
		return times[dataLayer];
	}
}
