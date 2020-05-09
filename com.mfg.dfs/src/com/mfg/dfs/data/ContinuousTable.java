package com.mfg.dfs.data;

import static com.mfg.utils.Utils.debug_var;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsEmptyDatabaseException;
import com.mfg.common.DfsInvalidRangeException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * A continuous view of a particular symbol and of a particular bar type.
 * 
 * <p>
 * This "table" is actually a "view", because it does not contain data. But it
 * has been called "table" because in some way it is a table, or, better, a
 * collection of tables glued with a particular logic, which is the logic of the
 * continous contract.
 * 
 * <p>
 * A continuous table has a link to the {@linkplain SymbolData} which is used to
 * contain the real data of this table
 * 
 * @author Sergio
 * 
 */
public class ContinuousTable extends SingleWidthTable {

	/**
	 * The new chunk does not store the time of the crossover, as this is not
	 * dependent on the type of table, the crossover takes place for all the
	 * types of table, no matter what.
	 * 
	 * <p>
	 * What is different however, is that every table has the "first" bar of the
	 * chunk different: this because after midnight (the crossover is on daily
	 * bars and daily bars starts at midnight) the first minute or range bars
	 * could be different.
	 * 
	 * <p>
	 * Moreover if the crossover has been on Friday, the next day is saturday,
	 * but maybe the first bar will be on monday.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	public static final class ContinuousChunk2 implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3031610824618383288L;

		/**
		 * This is the maturity which is current in this chunk, this is not
		 * really necessary, it is here just as a confirmation when we receive a
		 * new cross over.
		 */
		public final Maturity maturity;

		/**
		 * This is the zero based index of the continous table, this is used to
		 * fast search the chunk when the client asks for a bar at a determined
		 * zero based index in the continuous table (when the client asks for a
		 * bar at a certain date the chunks in the {@link SymbolData} are used
		 * instead)
		 * 
		 * <p>
		 * The first chunk has an index of zero.
		 */
		public int index = 0;

		/**
		 * This is the starting index in the table, it is valid until the next
		 * chunk is valid, in our case the next chunk will give to us the next
		 * valid maturity (table, ecc).
		 */
		public int idxInTable;

		/**
		 * the maximum index valid for this chunk.
		 */
		public int maxIndex = Integer.MAX_VALUE;

		/**
		 * Every chunk has the reference to the crossover object which has
		 * originated it. With this reference the table is able to access the
		 * real tables and the price offset valid for this crossover.
		 */
		public final CrossoverData crossData;

		public ContinuousChunk2(Maturity aMaturity, CrossoverData aCrossoverData) {
			crossData = aCrossoverData;
			maturity = aMaturity;
		}

		@Override
		public String toString() {
			return "{ " + this.crossData + " idxTable " + this.idxInTable
					+ " from: " + this.index + " to: " + this.maxIndex + "}";
		}

	}

	/**
	 * this is the tuple used to get an answer when the table finds a chunk with
	 * a particular time
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	protected static final class GetCCAns2 extends
			U.Tuple<Integer, ContinuousChunk2> {
		//
	}

	/**
	 * The last key is used to notice when the table is updated.
	 */
	transient int _previousLastKey = Integer.MIN_VALUE;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6771599413037105326L;

	/**
	 * eventually this new cont table will be the only one, for now I have to
	 * store them together.
	 */
	private ArrayList<ContinuousChunk2> _contTable2 = new ArrayList<>();

	/**
	 * a continous table does not store any data, so in the constructor it will
	 * need a reference to the real table
	 * 
	 * <p>
	 * The continuous table is referring to real data from the feed, so by
	 * definition it has a width of one.
	 * 
	 * @param symbolData
	 * @param aType
	 */
	public ContinuousTable(DfsSymbol symbol, BarType aType) {
		super(symbol, aType, 1);
	}

	/**
	 * Checks the coherence of table, returns an error message different from
	 * null if the table is not coherent.
	 * 
	 * @param aSymbolData
	 * 
	 * @return a string if there is an error, null if the table is OK.
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	private String _checkCoherenceOfTable(ContinuousData contData) {

		U.debug_var(193984, getKey(), " checking coherence of continuous table");

		try {

			/*
			 * First rough check. The last chunck should be equal
			 */
			if (_contTable2.size() != 0) {
				long lastXDate = contData.getLastCrossOverDate();
				long myLastXDate = _contTable2.get(_contTable2.size() - 1).crossData.crossDate;

				if (myLastXDate > lastXDate) {
					return U.join(getKey(), " x date ", new Date(myLastXDate),
							" is greater than ", new Date(lastXDate));
				} else if (myLastXDate < lastXDate) {
					return U.join(getKey(), " x date ", new Date(myLastXDate),
							" is smaller than ", new Date(lastXDate));
				}

				// all ok
				U.debug_var(
						938285,
						"ALL OK, My last x date is compatible with the cont data ",
						new Date(lastXDate));
			}

			/*
			 * checking coherence in the continuous table means to check the
			 * coherence of the glues which have been found to make it.
			 */
			ContinuousChunk2 ccPrev = null;
			for (ContinuousChunk2 ccCur : _contTable2) {
				U.debug_var(192098, getKey(), " checking chunk ", ccCur);

				/*
				 * Checking if the table is left truncated: if this is so then
				 * the table itself should start before the chunk, otherwise the
				 * previous chunks are unusable.
				 */
				SingleWidthTable curTable = ccCur.crossData.newMaturity
						.getTable(_type);

				if (curTable.isLeftTruncated()) {
					if (curTable.getStartingTime() >= ccCur.crossData.crossDate) {
						return U.join(
								"New table is left truncated with start ",
								new Date(curTable.getStartingTime()),
								" over cross date ", ccCur.crossData.crossDate);
					}
				}

				if (curTable.size() <= ccCur.idxInTable) {
					return U.join(curTable.getKey(),
							" probably has been truncated, its size is ",
							curTable.size());
				}

				// SingleWidthTable curTable = ccCur.crossData.newMaturity
				// .getTable(_type);
				Bar firstBarNextChunk = curTable.getBarAbs(ccCur.idxInTable);

				/*
				 * I want to make sure that the first bar of this chunk STARTS
				 * after cross date.
				 */
				long startingTimeFirstChunkBar;

				Bar beforeTheFirstChunkBar = null;
				if (ccCur.idxInTable != 0 && _type != BarType.RANGE) {
					beforeTheFirstChunkBar = curTable
							.getBarAbs(ccCur.idxInTable - 1);
				} else if (_type == BarType.RANGE && ccCur.idxInTable >= 1) {
					beforeTheFirstChunkBar = curTable
							.getBarAbs(ccCur.idxInTable - 1);
				}

				switch (_type) {
				case DAILY:
					startingTimeFirstChunkBar = firstBarNextChunk.getTime();
					break;
				case MINUTE:
					startingTimeFirstChunkBar = firstBarNextChunk.getTime()
							- Yadc.ONE_MINUTE_MSEC;
					break;
				case RANGE:
					if (beforeTheFirstChunkBar == null)
						return "before the first chunk bar is null";
					startingTimeFirstChunkBar = beforeTheFirstChunkBar
							.getTime();
					break;
				default:
					return "abnormal state" + _type;

				}

				if (startingTimeFirstChunkBar < ccCur.crossData.crossDate) {
					return " first bar " + new Date(startingTimeFirstChunkBar)
							+ " is before the cross date " + ccCur;
				}

				if (_type == BarType.DAILY) {
					if (firstBarNextChunk.getTime() < ccCur.crossData.crossDate) {
						return (firstBarNextChunk + " < " + ccCur);
					}
					if (beforeTheFirstChunkBar != null
							&& beforeTheFirstChunkBar.getTime() >= ccCur.crossData.crossDate) {
						return (beforeTheFirstChunkBar + " >= " + ccCur);
					}
				} else {
					if (firstBarNextChunk.getTime() <= ccCur.crossData.crossDate) {
						return (ccCur + " <= " + ccCur);
					}
					// range bars are different, in that case we may have that
					// the
					// previous bar ends after the crossdate
					if (_type == BarType.MINUTE) {
						if (beforeTheFirstChunkBar != null
								&& beforeTheFirstChunkBar.getTime() > ccCur.crossData.crossDate) {
							return (beforeTheFirstChunkBar + " > " + ccCur);
						}
					}
				}

				if (ccPrev != null) {

					/*
					 * the maturity switch must be coherent
					 */
					if (!ccPrev.crossData.newMaturity.getMaturity().equals(
							ccCur.crossData.oldMaturity.getMaturity())) {
						return "incoherent pass from " + ccPrev + " to "
								+ ccCur;
					}

					/*
					 * the last of the prev chunk must come before the first of
					 * the current chunk, and the next of the previous chunk
					 * should start after the cross date of the current chunk
					 */
					SingleWidthTable lastTable = ccPrev.crossData.newMaturity
							.getTable(_type);
					int lastPrevIndex = ccPrev.idxInTable
							+ (ccPrev.maxIndex - ccPrev.index);
					Bar lastBarPrevChunk = lastTable.getBarAbs(lastPrevIndex);
					/*
					 * very simple check, the two bars of the two chunks must be
					 * in order.
					 */
					if (firstBarNextChunk.getTime() <= lastBarPrevChunk
							.getTime()) {

						U.debug_var(392952, "first bar ", firstBarNextChunk,
								" last ", lastBarPrevChunk);
						return "chunk times mismatch "
								+ firstBarNextChunk.getTime() + " last bar "
								+ lastBarPrevChunk.getTime();
					}

					long endingTimePrevChunk = lastBarPrevChunk.getTime();
					if (_type == BarType.DAILY) {
						endingTimePrevChunk += Yadc.ONE_DAY_MSEC;
					}

					/*
					 * The first is a crude check about the ending time of the
					 * last bar of the previous chunk, of course this must be
					 * not higher of the cross date
					 */
					if (endingTimePrevChunk > ccCur.crossData.crossDate) {
						return (lastBarPrevChunk + " > " + ccCur);
					}

					/*
					 * Then there is a more sophisticated check about the ending
					 * time of the next bar, if it is defined. The ending time
					 * of the next bar must be greater than the cross date.
					 */
					long endingTimeOfNextPrevChunkBar;

					if (lastPrevIndex < lastTable.upLimit()) {
						Bar nextPrevBar = lastTable
								.getBarAbs(lastPrevIndex + 1);

						endingTimeOfNextPrevChunkBar = nextPrevBar.getTime();
						if (_type == BarType.DAILY) {
							endingTimeOfNextPrevChunkBar += Yadc.ONE_DAY_MSEC;
						}

						if (endingTimeOfNextPrevChunkBar <= ccCur.crossData.crossDate) {
							return ("xxdw previous chunk after cross bar "
									+ nextPrevBar + " comes not after " + ccCur);
						}
					}

				}
				ccPrev = ccCur;
			}
		} catch (DFSException e) {
			return "exception during check computation " + e.toString();
		}

		return null; // all fine
	}

	private ContinuousChunk2 _getCcWhichContainsIndex(int i) {

		if (_contTable2.size() == 0) {
			throw new ArrayIndexOutOfBoundsException(getKey() + " Searched "
					+ i + " size zero");
		}

		if (i < 0 || i > upLimit()) {
			throw new ArrayIndexOutOfBoundsException(getKey() + " Searched "
					+ i + " size " + size());
		}

		int low, high, mid;
		low = 0;
		high = _contTable2.size() - 1;

		while (low <= high) {
			mid = (low + high) / 2;
			ContinuousChunk2 cc = _contTable2.get(mid);
			if (cc.maxIndex >= i && cc.index <= i) {
				return cc;
			}

			// no good! Let's see if we have to go down or up
			if (i < cc.index) {
				// do down
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		U.dump(this);
		throw new IllegalStateException(getKey()
				+ " Cannot find chunk which contains " + i);
	}

	/**
	 * returns the continuous chunk which contains a given time.
	 * 
	 * <p>
	 * A chunk has a definite floor, which is the time of the crossover taking
	 * place in that chunk, so the chunk returned will have a cross time equal
	 * or less than the parameter.
	 * 
	 * <p>
	 * The end is defined differently, because the end is simply the time of the
	 * last bar of the chunk, if it is a minute or range bar, or the time of the
	 * last bar of the chunk PLUS a <b>day</b> if the bar is daily, because the
	 * daily bar has a time equal to the beginning of the period.
	 * 
	 * <p>
	 * In other words we have that the chunk returned <b>will</b> have a bar
	 * whose time is nearest to the time given and there is <b>nowhere</b> in
	 * the table a bar which has a time nearest to the time given as this chunk.
	 * 
	 * <p>
	 * The problem may arise because the chunk has a definite start which is
	 * before the first bar (except for daily bars, because they have the
	 * starting time equal to the time of the bar).
	 * 
	 * @param aTime
	 * @return a tuple containing the index and the chunck which contains a bar
	 *         nearest to this time inside the first and the last
	 *         <P>
	 *         If this chunk has only one bar then the search is exact
	 * 
	 * @throws DFSException
	 */
	@SuppressWarnings({ "boxing" })
	private GetCCAns2 _getCCWhichContainsTime(long aTime) throws DFSException {
		GetCCAns2 cc2 = new GetCCAns2();
		int low, high, mid;
		low = 0;
		high = _contTable2.size() - 1;

		while (low <= high) {
			mid = (low + high) / 2;
			ContinuousChunk2 cc = _contTable2.get(mid);

			boolean insideLastBar = false;
			long lastKey;
			if (mid != _contTable2.size() - 1) {
				lastKey = cc.crossData.newMaturity.getTable(_type)
						.getEndingTimeOfBarAt(
								cc.idxInTable + (cc.maxIndex - cc.index));
			} else { // the last table is taken fully
				lastKey = cc.crossData.newMaturity.getTable(_type)
						.getEndingTime();
			}

			long startOfChunk;// = cc.crossData.crossDate;

			/*
			 * In case this chunk is not the first the start of the chunk is
			 * simply the ending time of the last chunk
			 */
			if (mid != 0 /* && _type == BarType.RANGE */) {
				ContinuousChunk2 ccMinus = _contTable2.get(mid - 1);
				startOfChunk = ccMinus.crossData.newMaturity.getTable(_type)
						.getEndingTimeOfBarAt(
								ccMinus.idxInTable
										+ (ccMinus.maxIndex - ccMinus.index));
			} else {
				/*
				 * The starting time of the first chunk is the starting time of
				 * the bar at index first (zero or one, one if the type is
				 * range).
				 */
				startOfChunk = cc.crossData.newMaturity.getTable(_type)
						.getStartingTimeOfBarAt(cc.idxInTable);
			}

			// out of bounds check
			if (mid == 0 && aTime < startOfChunk) {
				throw new DFSException("The time searched " + new Date(aTime)
						+ " is before the first bar " + new Date(startOfChunk));
			} else if (aTime > lastKey && mid == _contTable2.size() - 1) {
				throw new DFSException("The time searched " + aTime
						+ " is after the last bar " + lastKey + " "
						+ new Date(lastKey));
			}

			if (aTime <= lastKey) {
				insideLastBar = true;
			}

			if (startOfChunk <= aTime && insideLastBar) {
				cc2.f1 = mid;
				cc2.f2 = cc;
				return cc2;
			}

			// no good! Let's see if we have to go down or up
			if (aTime < startOfChunk) {
				// do down
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		throw new IllegalStateException(getKey()
				+ " Cannot find chunk which contains " + new Date(aTime)
				+ " my last is " + new Date(getEndingTime()));
	}

	/**
	 * Simple helper method which is used to get the table in a chunk.
	 * <p>
	 * This is then used to compute the various indeces.
	 * 
	 * @param aCC
	 * @return
	 */
	private HistoryTable _getTableInChunk(ContinuousChunk2 aCC) {
		return (HistoryTable) aCC.crossData.newMaturity.getTable(_type);
	}

	@Override
	protected void _truncateImpl(long truncateDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		// close is a no-op for this table.
	}

	@Override
	public boolean doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws DFSException {

		// boolean isMultipleCheck = false;
		if (CHECK_MULTIPLE_TABLES) {
			_checkCoherenceOfMultipleTable();
		}

		if (isFromScheduler) {
			debug_var(839942, getKey(),
					" let's see if I can expand the continuous table");
			if (_contTable2.size() == 0) {
				debug_var(381934, getKey(), " Empty table, nothing to do");
				return false;
			}

			String err = _checkCoherenceOfTable(aSymbolData.getContData());
			if (err != null) {
				U.debug_var(193958, "coherence mismatch ", err);
				_needsRecompute = true;
			}
		} else if (_atLeastOneViewIsOpen()) {
			// Ok, that means that I have to "hold" the table corresponding to
			// the last maturity of this type
			if (_contTable2.size() == 0) {
				return false;
			}
			// _previousLastKey
			SingleWidthTable table = _contTable2.get(_contTable2.size() - 1).crossData.newMaturity
					.getTable(_type);
			table.forceUpdate();

			// also the continuous table can have multiple tables on top of
			// it...
			_updateMultipleTables();

			int currentUpLimit = this.upLimit();
			if (_previousLastKey != currentUpLimit) {
				_previousLastKey = currentUpLimit;
				return true;
			}
		}
		return false;

	}

	public void endCrossOverComputation(ContinuousData contData)
			throws DFSException {

		String error = _checkCoherenceOfTable(contData);
		if (error != null) {
			throw new DFSException(error);
		}

	}

	@Override
	public void forceUpdate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bar getBarAbs(int i) throws DFSException {

		ContinuousChunk2 cc = _getCcWhichContainsIndex(i);

		/*
		 * The cc.index is the starting index of this chunk which corresponds to
		 * cc.idxInTable.
		 * 
		 * So, if I want an index i > cc.index, I must go upward in the table by
		 * delta_i.
		 * 
		 * Supposing that this is the last chunk the maximum index will be:
		 * 
		 * size()-1
		 */

		// Ok, now I get the index in table
		int delta_i = i - cc.index;
		int ht_idx = cc.idxInTable + delta_i;

		Bar aBar = cc.crossData.newMaturity.getTable(_type).getBarAbs(ht_idx);

		// offsetting price!
		int offset = cc.crossData.getPriceOffset();

		aBar.offsetsPrices(offset);

		return aBar;
	}

	@Override
	@SuppressWarnings("boxing")
	public synchronized int getBarsBetween(long startDate, long endDate)
			throws DFSException {
		// U.debug_var(103910, getKey(), " gbb ", new Date(startDate), " to ",
		// new Date(endDate));

		GetCCAns2 indexOfStartCC;
		GetCCAns2 indexOfEndCC;

		if (size() == 0) {
			throw new DfsEmptyDatabaseException("empty db, start "
					+ new Date(startDate) + " end " + new Date(endDate));
		}

		indexOfStartCC = _getCCWhichContainsTime(startDate);
		indexOfEndCC = _getCCWhichContainsTime(endDate);

		ContinuousChunk2 ccStart = indexOfStartCC.f2;
		ContinuousChunk2 ccEnd = indexOfEndCC.f2;

		if (indexOfStartCC.f1.equals(indexOfEndCC.f1)) {
			// ok, they are the same, so I simply ask the chunk
			debug_var(981933, "SAME MATURITY ... ", ccEnd.crossData.newMaturity);
			return indexOfStartCC.f2.crossData.newMaturity.getBarsBetween(
					_type, 1, startDate, endDate);
		}

		/*
		 * I must approach the start date from the future, so I must find the
		 * bar whose start time is equal or higher than this time, i.e. the
		 * floor.
		 * 
		 * the next chunk is defined, otherwise we would have had f1 == f2,
		 * which was the previous case.
		 */
		int indexNextChunk = indexOfStartCC.f1 + 1;
		long startDateNextChunk = _contTable2.get(indexNextChunk).crossData.crossDate;

		SingleWidthTable beginTable = ccStart.crossData.newMaturity
				.getTable(_type);
		long normalizedEnd = Math.min(beginTable.getEndingTime(),
				startDateNextChunk);
		long deltaStart = beginTable.getBarsBetween(startDate, normalizedEnd);

		/*
		 * The delta end is the number of bars present in the final chunk of
		 * this continuous table. The starting time of the chunk is by
		 * definition the start date.
		 */
		SingleWidthTable destTable = ccEnd.crossData.newMaturity
				.getTable(_type);
		long normalizedStart = Math.max(ccEnd.crossData.crossDate,
				destTable.getStartingTime());
		long deltaEnd = destTable.getBarsBetween(normalizedStart, endDate);

		int deltaBars = 0;

		indexOfStartCC.f1++; // this because the first chunk is already
								// computed.
		for (int i = indexOfStartCC.f1; i < indexOfEndCC.f1; ++i) {
			// Ok, You should get the number of bars of this chunk
			ContinuousChunk2 cc = _contTable2.get(i);
			deltaBars += cc.maxIndex - cc.index + 1;
		}

		// debug_var(993913, getKey(), " gbb DeltaStart ", deltaStart,
		// " dbars ",
		// deltaBars, " dEnd ", deltaEnd);
		return (int) (deltaStart + deltaBars + deltaEnd);
	}

	@Override
	public Bar getBarStartingAtTime(long time) throws DFSException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final IBarCache getCache(RequestParams aReq) throws DFSException {
		/*
		 * I have to force the update of the linked real table.
		 */
		if (_contTable2.size() != 0) {
			_contTable2.get(_contTable2.size() - 1).crossData.newMaturity
					.getTable(_type).forceUpdate();
		}

		return super.getCache(aReq);

	}

	public Maturity getCurrentMaturity() throws DFSException {
		if (_contTable2.size() == 0) {
			throw new DFSException(
					"The cont. table is empty, there is not a current maturity");
		}
		return _contTable2.get(_contTable2.size() - 1).maturity;
	}

	@Override
	public long getDateAfterXBarsFrom(long startDate, int numBars)
			throws DFSException {

		GetCCAns2 indexOfStartCC = _getCCWhichContainsTime(startDate);

		int idxInTable = indexOfStartCC.f2.crossData.newMaturity
				.getTable(_type).getMinimumIndexOfBarAfter(startDate);

		int continuousIndex = indexOfStartCC.f2.index
				+ (idxInTable - indexOfStartCC.f2.idxInTable);

		int destinationIndex = continuousIndex + numBars - 1;

		if (destinationIndex > upLimit()) {
			throw new DfsInvalidRangeException(getKey()
					+ " Outside range Maximum allowed "
					+ (upLimit() - continuousIndex + 1));
		}

		// Now I have to take the destination chunk
		ContinuousChunk2 destChunk = _getCcWhichContainsIndex(destinationIndex);

		int idxInEndingTable = destChunk.idxInTable
				+ (destinationIndex - destChunk.index);

		long endingTimeInTable = destChunk.crossData.newMaturity
				.getTable(_type).getEndingTimeOfBarAt(idxInEndingTable);

		return endingTimeInTable;

	}

	@Override
	public long getDateBeforeXBarsFrom(long endTime, int numBars)
			throws DFSException {

		GetCCAns2 indexOfEndCC = _getCCWhichContainsTime(endTime);

		int idxInTable = indexOfEndCC.f2.crossData.newMaturity.getTable(_type)
				.getMaximumIndexOfBarWithin(endTime);

		int continuousIndex = indexOfEndCC.f2.index
				+ (idxInTable - indexOfEndCC.f2.idxInTable);

		int destinationIndex = continuousIndex - numBars + 1;

		if (destinationIndex < 0) {
			throw new DfsInvalidRangeException(getKey()
					+ " Outside range Maximum allowed " + (continuousIndex + 1));
		}

		// Now I have to take the destination chunk
		ContinuousChunk2 destChunk = _getCcWhichContainsIndex(destinationIndex);

		int idxInEndingTable = destChunk.idxInTable
				+ (destinationIndex - destChunk.index);

		long startingTimeInTable = destChunk.crossData.newMaturity.getTable(
				_type).getStartingTimeOfBarAt(idxInEndingTable);

		return startingTimeInTable;

	}

	@Override
	public long getEndingTime() throws DFSException {
		return getEndingTimeOfBarAt(upLimit());
	}

	@Override
	public long getEndingTimeOfBarAt(int aIndex) throws DFSException {
		ContinuousChunk2 cc2 = _getCcWhichContainsIndex(aIndex);
		return cc2.crossData.newMaturity.getTable(_type).getEndingTimeOfBarAt(
				cc2.idxInTable + (aIndex - cc2.index));
	}

	@Override
	public String getKey() {
		return "[ " + _symbol + Maturity.CONTINUOUS_SUFFIX + " " + _type + "]";
	}

	@Override
	public Maturity getMaturity() {
		return null; // the null maturity is the continuous maturity
	}

	@Override
	public int getMaximumIndexOfBarWithin(long cDate) throws DFSException {
		/*
		 * To the the maximum within first of all I have to find the chunk which
		 * contains this date, then search inside the real table and then look
		 * if the real table has a index which is useful otherwise I have to go
		 * back of one chunk.
		 */
		GetCCAns2 cc2 = _getCCWhichContainsTime(cDate);

		SingleWidthTable newTable = cc2.f2.crossData.newMaturity
				.getTable(_type);
		int tableIndex = newTable.getMaximumIndexOfBarWithin(cDate);

		assert (tableIndex >= cc2.f2.idxInTable);
		assert (tableIndex <= cc2.f2.idxInTable
				+ (cc2.f2.maxIndex == Integer.MAX_VALUE ? (newTable.size()
						- cc2.f2.idxInTable - 1)
						: (cc2.f2.maxIndex - cc2.f2.index)));

		return cc2.f2.index + (tableIndex - cc2.f2.idxInTable);
	}

	@Override
	public int getMinimumIndexOfBarAfter(long cDate) throws DFSException {
		GetCCAns2 cc2 = _getCCWhichContainsTime(cDate);

		SingleWidthTable newTable = cc2.f2.crossData.newMaturity
				.getTable(_type);
		int tableIndex = newTable.getMinimumIndexOfBarAfter(cDate);

		assert (tableIndex >= cc2.f2.idxInTable);
		assert (tableIndex <= cc2.f2.idxInTable
				+ (cc2.f2.maxIndex == Integer.MAX_VALUE ? (newTable.size()
						- cc2.f2.idxInTable - 1)
						: (cc2.f2.maxIndex - cc2.f2.index)));

		return cc2.f2.index + (tableIndex - cc2.f2.idxInTable);
	}

	@Override
	public long getStartingTime() throws DFSException {
		return getStartingTimeOfBarAt(0);
	}

	@Override
	public long getStartingTimeOfBarAt(int aIndex) throws DFSException {
		ContinuousChunk2 cc2 = _getCcWhichContainsIndex(aIndex);
		return cc2.crossData.newMaturity.getTable(_type)
				.getStartingTimeOfBarAt(cc2.idxInTable + (aIndex - cc2.index));
	}

	@Override
	public DfsIntervalStats getStats(boolean forceCheck) {
		int numBars = size();
		long startDate;
		long endDate;

		if (numBars == 0) {
			startDate = -1;
			endDate = -1;
		} else {
			try {
				startDate = this.getStartingTime();
				endDate = getEndingTime();

				if (forceCheck) {
					_checkStatCoherence(numBars, startDate, endDate);
				}

			} catch (DfsInvalidRangeException e) {
				// If you arrive here then the cache is good but you don't have
				// the
				// bars, So I safely turn off here
				// the result.
				startDate = -1;
				endDate = -1;
				numBars = 0;
			} catch (DFSException e) {
				// this could be a more serious error.
				e.printStackTrace();
				startDate = -2;
				endDate = -2;
				numBars = 0;
			}

		}
		return new DfsIntervalStats(EVisibleState.UP_TO_DATE, numBars,
				startDate, endDate);
	}

	@Override
	public boolean isEmpty() {
		return _contTable2.size() == 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	/**
	 * Called when a new cross over comes, either from the historical part or
	 * the real time part.
	 * 
	 * <p>
	 * This event simply creates a new chunk in the continuous table, updating
	 * the last one (because there are the upper limits which must be changed).
	 * 
	 * @param cd
	 *            the x-over received from below, this is the initial time from
	 *            which data is valid for this chunk
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	public void newCrossOver(CrossoverData cd) throws DFSException {
		/*
		 * The idea is simple, I fill a new chunk using the information of the
		 * new maturity that takes over in this period.
		 */

		ContinuousChunk2 oldChunk = null;
		SingleWidthTable oldTable = null;
		// This must be always different from null.
		SingleWidthTable newTable = cd.newMaturity.getTable(_type);

		if (newTable.size() == 0) {
			U.debug_var(919301, getKey(), " Cannot use empty table ",
					newTable.getKey(), " my actual chunk size is ",
					_contTable2.size(), " for ", cd);
			// this._contTable2.remove(_contTable2.size() - 1);
			return;
		}

		ContinuousChunk2 cc2 = new ContinuousChunk2(
				cd.newMaturity.getMaturity(), cd);

		/**
		 * This is the previous chunk size, if it is defined.
		 */
		int prevChunkSize = 0;
		long cDate = cd.crossDate;

		if (_contTable2.size() != 0) {
			/*
			 * I have to adjust the previous chunk.
			 */

			oldChunk = _contTable2.get(_contTable2.size() - 1);
			// Ok, now I simply have to get the index in table
			/*
			 * In theory these two maturities should be equal.
			 * 
			 * assert (oldChunk.maturity.equals(cd.oldMaturity.getMaturity()));
			 * 
			 * but there may be an hole which prevents the assert from being
			 * satisfied; this means that the old table is the one from the
			 * chunk, not the one which is imposed by the cross over.
			 */
			if (!oldChunk.maturity.equals(cd.oldMaturity.getMaturity())) {
				U.debug_var(190091, getKey(), "lack of correspondence from ",
						oldChunk.maturity, " to ",
						cd.oldMaturity.getMaturity(), " the former wins!");
				_contTable2.clear();
				cc2.index = 0; // returning to origin
				oldChunk = null; // forget it
			} else {
				oldTable = oldChunk.crossData.newMaturity.getTable(_type);

				int prevCeiling = oldTable.getMaximumIndexOfBarWithin(cDate);

				/*
				 * Now I have a bar whose starting time is lower than cDate...
				 * is this bar also ending before cDate? I have to check
				 */
				if (prevCeiling < 0) {
					/*
					 * This means that all the bars are after cDate, the
					 * previous chunk cannot contain any bar
					 */
					U.debug_var(717387, getKey(), " cannot use ", oldTable,
							" because it has all bars higher than xDate ",
							new Date(cDate), ". Cur size is: ",
							_contTable2.size());
					_contTable2.remove(_contTable2.size() - 1);
					// reset the maximum index
					if (_contTable2.size() != 0) {
						_contTable2.get(_contTable2.size() - 1).maxIndex = Integer.MAX_VALUE;
					}
					return;
				}

				prevChunkSize = prevCeiling - oldChunk.idxInTable + 1;
				oldChunk.maxIndex = oldChunk.index + prevChunkSize - 1;
				cc2.index = oldChunk.maxIndex + 1;

				/*
				 * Checking if the glue has a meaning... if the two tables, old
				 * and new can overlap. The new table can also be truncated but
				 * if its starting time is after the cross date it is OK,
				 * otherwise the previous chunks are unusable
				 */
				if (newTable.isLeftTruncated()) {
					if (newTable.getStartingTime() >= cd.crossDate) {
						U.debug_var(173910,
								"New table is left truncated with start ",
								new Date(newTable.getStartingTime()),
								" over cross date ", cd);
						_contTable2.clear();
						cc2.index = 0; // returning to origin
						oldChunk = null; // forget it
					}
				}
			}

		} else { // conttable 2 empty
			cc2.index = 0;
		}

		int indexInNewTable;// = newTable.getCeilingIndexFor(cDate - 1);

		indexInNewTable = newTable.getMinimumIndexOfBarAfter(cDate);

		if (indexInNewTable < 0) {
			/*
			 * This fact unconditionally means that this chunk is not valid for
			 * the table, because the maturity has no data after the cross date.
			 * This is a bad fact if there are other chunks after, in which case
			 * the continuous table will be truncated because there is not the
			 * maturity continuity. In this case I simply pass over, and I
			 * cannot use this chunk.
			 */
			U.debug_var(
					281932,
					getKey(),
					" no data for cross over ",
					cd,
					" I pass. table is finishes at ",
					newTable.size() == 0 ? " [EMPTY] " : new Date(newTable
							.getEndingTime()));

			// reset the maximum index
			if (_contTable2.size() != 0) {
				_contTable2.get(_contTable2.size() - 1).maxIndex = Integer.MAX_VALUE;
			}
			return;
		}

		cc2.idxInTable = indexInNewTable;

		/*
		 * Consistency check
		 */
		if (oldChunk != null) {
			assert (oldChunk.maxIndex == cc2.index - 1);
		}

		if (cc2.idxInTable < 0) {
			assert (false); // simple check
		}
		_contTable2.add(cc2);
	}

	@Override
	protected Object readResolve() throws DFSException {
		super.readResolve();
		if (_contTable2 == null) {
			_contTable2 = new ArrayList<>();
		}
		return this;
	}

	@Override
	public int size() {
		return upLimit() + 1;
	}

	public void startCrossOverComputation() {
		_contTable2.clear();
	}

	@Override
	public String toString() {
		return "[" + getKey() + "]";
	}

	@Override
	public int upLimit() {
		if (_contTable2.size() == 0) {
			return -1; // the cc table is empty (maybe for an invalid symbol).
		}

		// the last table is taken fully.
		ContinuousChunk2 lastChunk = _contTable2.get(_contTable2.size() - 1);
		HistoryTable lastTable = _getTableInChunk(lastChunk);
		/*
		 * I ask the up limit because for the range table the up limit is not
		 * cut off
		 */
		return lastChunk.index + (lastTable.upLimit() - lastChunk.idxInTable);
	}

}
