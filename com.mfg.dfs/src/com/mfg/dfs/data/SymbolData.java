package com.mfg.dfs.data;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsEmptyDatabaseException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * This is the class which holds the data for a particular symbol.
 * 
 * <p>
 * It has all the logic to build the continuous contract using the data from the
 * data provider.
 * 
 * <p>
 * The idea is that the data is frozen in the data provider (historically) and
 * that we may have the possibility to store the data in the SymbolData table.
 * 
 * <p>
 * 
 * @author Sergio
 * 
 */
public class SymbolData extends BaseSymbolData {

	/**
	 * Here I list the states for the symbol data FSM.
	 * 
	 * 
	 * @author Sergio
	 * 
	 */
	private enum EState {
		/**
		 * The initial state, nothing to be worried about that, it is only used
		 * when first the application is brought up.
		 */
		BLANK,
		/**
		 * In this state I collect the data for the current maturity, which is
		 * the current contract used by the system (it should be, unless of some
		 * specific dates, for example from March 10th to March 21st the
		 * contract is in some way unspecified, because we have the problem of
		 * the rollover.
		 * 
		 * <p>
		 * We may have a different rollover in the system and for our rules (the
		 * number of contracts traded).
		 */
		COLLECT_CURRENT_MATURITY,

		/**
		 * In this state I collect the data for the past maturities which are
		 * then used to
		 * 
		 */
		COLLECT_PAST_MATURITIES,

		/**
		 * This is the state when I collect the future maturites,
		 */
		COLLECT_FUTURE_MATURITIES,

		/**
		 * The state when I compute the cut offs, which are used to define the
		 * continous contract.
		 */
		// COMPUTE_CUT_OFFS,

		/**
		 * This is the "normal" state for the symbol data, here I will remain
		 * for ever, but I will try to look at the next maturity, to add it to
		 * the list of the managed maturities.
		 */
		READY,
		/**
		 * From this state the maturity is then put away, because we have had
		 * some problems. (later we could add some recovery mechanism, if
		 * possible).
		 */
		ABORT,
		/**
		 * Just a temporary state to let the symbol compute the cut off in the
		 * normal cycle.
		 */
		COMPUTE_CUT_OFFS,

	}

	/**
	 * A simple struct which holds the max maturity
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private static class MaxMaturity {

		public final Maturity maturity;

		public final int closePrice;
		public final int volume;

		MaxMaturity(Maturity aMaturity, int aClose, int aVolume) {
			maturity = aMaturity;
			closePrice = aClose;
			volume = aVolume;
		}

		@Override
		public String toString() {
			return "{" + maturity + "," + closePrice + "," + volume + "}";
		}
	}

	/**
	 * This is a class which integrates the given maturities for a given day.
	 * 
	 * <P>
	 * The fact is that the continuous contract is always one day late, because
	 * we compute the list after all the daily bars are formed, that is after
	 * the day is over. When we have a crossover, the crossover starts from the
	 * midnight of the next day.
	 * <p>
	 * This is inevitable, during the historical building of the continuous
	 * contract, however, the dates could be exact, that is to start the
	 * contract before, but this is only right during the historical part.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private static class MaxMaturityDayCollector {

		public final Date date;

		// the list of the maturities which have data for a given day.
		public final ArrayList<MaxMaturity> maxMaturities = new ArrayList<>();

		public MaxMaturityDayCollector(Date aDate) {
			date = (Date) aDate.clone();
		}

		/**
		 * Accepts the given maturity, the close and the volume for a given
		 * date.
		 * 
		 * @param aMat
		 * @param closePrice
		 * @param volume
		 */
		public void accept(Maturity aMat, int closePrice, int volume) {

			int indexToAdd = 0;
			for (; indexToAdd < maxMaturities.size(); ++indexToAdd) {
				MaxMaturity mm = maxMaturities.get(indexToAdd);
				if (mm.volume < volume) {
					break; // this is the index to break.
				}
			}
			maxMaturities.add(indexToAdd, new MaxMaturity(aMat, closePrice,
					volume));
		}

		/**
		 * returns true is this winner maturity is different from the winner
		 * maturity of the given argument.
		 * 
		 * @param maxMatPrev
		 *            another collector
		 * @return true if the maturity is changed
		 * 
		 * @throws IndexOutOfBoundsException
		 *             if the collector does not contain a maturity.
		 */
		public boolean changedMaturity(MaxMaturityDayCollector maxMatPrev) {
			return !this.maxMaturities.get(0).maturity
					.equals(maxMatPrev.maxMaturities.get(0).maturity);
		}

		/**
		 * searches inside the list of these maturities and tries to find the
		 * maturity corresponding to the max maturity data given
		 * 
		 * @param maxMatData
		 * @return
		 */
		@SuppressWarnings({ "boxing", "unused" })
		public MaxMaturity findMaturity(MaxMaturityDayCollector maxMatData) {
			Maturity maxMat = maxMatData.maxMaturities.get(0).maturity;
			int i = 0;
			for (MaxMaturity item : maxMaturities) {
				U.debug_var(199130, "i = ", i, " item is ", item);
				if (item.maturity.equals(maxMat)) {
					U.debug_var(103013, "found item ", item, " @ position ", i);
					return item;
				}
				i++;
			}
			return null;
		}

		/**
		 * searches the winner of the supplied parameter between the losers of
		 * this collector (that is the items with index greater than 0).
		 * 
		 * <p>
		 * returns the losers, if it can be found, null otherwise
		 * 
		 * @param collector
		 *            a (previous) collector
		 * @return the item in the <b>current</b> collector whose maturity is
		 *         equal to the winner maturity of the parameter, null otherwise
		 */
		public MaxMaturity searchInLosers(MaxMaturityDayCollector collector) {
			Maturity mat = collector.maxMaturities.get(0).maturity;
			for (MaxMaturity item : this.maxMaturities.subList(1,
					this.maxMaturities.size())) {
				if (item.maturity.equals(mat)) {
					return item;
				}
			}
			return null; // not found
		}

		@Override
		public String toString() {
			return "[ "
					+ date
					+ " size "
					+ maxMaturities.size()
					+ " winner: "
					+ (maxMaturities.size() == 0 ? " Null " : maxMaturities
							.get(0)) + "]";
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8686846805893327522L;

	private static final String DEFAULT_TIME_ZONE = "America/New_York";

	/**
	 * Simple test method to assert the coherence of the crossover data.
	 * 
	 * @param crossOvers
	 *            the list of crossovers.
	 */
	private static void _checkCoherenceOfCrossOversSoFar(
			ArrayList<CrossoverData> crossOvers) {
		CrossoverData cdPrev = null;
		for (CrossoverData cdCur : crossOvers) {
			if (cdPrev != null) {
				if (cdCur.crossDate <= cdPrev.crossDate) {
					assert (false);
				}

				if (cdCur.oldMaturity.getMaturity().compareTo(
						cdPrev.newMaturity.getMaturity()) != 0) {
					assert (false) : U.join(cdCur.oldMaturity, " different ",
							cdPrev.newMaturity);
				}
			}
			cdPrev = cdCur;
		}

	}

	/**
	 * I have a list of quarters, and for each quarter I may have some data (not
	 * necessarily).
	 * <p>
	 * The data is then saved as a set of MDB tables, but this could change in
	 * the future.
	 * 
	 * <p>
	 * Key is the maturity object. The maturity be any quarter, for now only
	 * quarter maturities are supported (but the map will hold any maturity,
	 * even monthly).
	 * 
	 * <p>
	 * The symbol in cache has the possibility to create as many MaturityData as
	 * they are, but of course the limitation is the number of open files (this
	 * maybe a problem for the operating system, who knows, maybe I have to tell
	 * it).
	 */
	private TreeMap<Maturity, MaturityData> _data = new TreeMap<>();

	/**
	 * Every symbol has its own continuous view of the data.
	 * <p>
	 * The data is not really here but inside the treemap of maturities.
	 * 
	 */
	private ContinuousData _contData;

	/**
	 * The state of the symbol.
	 * <p>
	 * This state is used mainly in the initial phase
	 */
	private EState _state = EState.BLANK;

	/**
	 * This long stores the last time that we have checked the maturity and the
	 * cross over of the next maturity on this one. It is useful because
	 * otherwise we will check the crossing of the maturity on each moment.
	 */
	private long _lastMaturityCheck;

	/**
	 * The time zone of this symbol. The default is New York. This time zone is
	 * important because it determines the midnight when a new daily bar comes
	 * and the program can know if there has been a cross over.
	 * 
	 * THIS IS GOING TO BE REMOVED, April 1st, I will put it in the DfsSymbol
	 * structure.
	 */
	private String _timeZone = DEFAULT_TIME_ZONE;

	/**
	 * I build a symbol data which with a particular prefix (for example "ES").
	 * The class will then try to get all the data for this prefix from the data
	 * feed.
	 * <p>
	 * The class is a FSM and it is able to build data from partial snapshots of
	 * bars which are inside the datafeed.
	 * 
	 * <p>
	 * The class needs a dataFeed. The datafeed can be sequential but can also
	 * be parallel. The important thing is that it can be shared (but in any
	 * case the update thread --- the doOneStep thread --- is unique).
	 * 
	 * @param aFeed
	 *            This is the data feed which is used by all the descendants of
	 *            this object.
	 * 
	 */
	public SymbolData(DfsSymbol aSymbol) {
		super(aSymbol);
		// _symbol = aSymbol;
		_contData = new ContinuousData(aSymbol);
	}

	/**
	 * @param unused
	 *            unused, just to distinguish the constructor between the public
	 *            one.
	 */
	protected SymbolData(DfsSymbol aSymbol, boolean unused) {
		super(aSymbol);
		// _symbol = aSymbol;
	}

	private boolean _allEmptyMaturities() {

		for (MaturityData data : _data.values()) {
			if (!data.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * compares the daily volume of the current maturity and of the next valid
	 * maturity.
	 * 
	 * <p>
	 * If the next valid maturity has a higher volume then the new maturity is
	 * valid and the method will trigger the new offset computation.
	 * 
	 * @throws DFSException
	 * 
	 */
	private void _checkCrossOver() throws DFSException {

		long date = _contData.getLastCrossOverDate();
		_computeCutOffsSince(date, false);
	}

	private void _computeCutOffsComplete() {
		debug_var(381903, "compute the cut off for the maturities.");
		try {
			_computeCutOffsNew();
		} catch (DFSException e) {
			e.printStackTrace();
			_state = EState.ABORT;
		}
		_state = EState.COMPUTE_CUT_OFFS;
		_lastMaturityCheck = System.currentTimeMillis();
	}

	/**
	 * This is the new method to compute the cut offs, for now it is a tentative
	 * exploration using the informations which I have already stored.
	 * 
	 * @throws DFSException
	 */
	private void _computeCutOffsNew() throws DFSException {

		long lowerDate; // = _data.firstKey().getStartDate();
		lowerDate = _getMinimumDailyBarDate();
		_contData.startCrossOverComputation(); // this will clear the map
		_computeCutOffsSince(lowerDate, true);

	}

	/**
	 * Computes the cut offs since a particular date. It does not clear the map
	 * of the continuous data, this is done only by the complete cut off
	 * computation, the {@link #_computeCutOffsNew()}
	 * 
	 * @param lowerDate
	 * @param feedTheFirst
	 *            true if you want to feed the first crossover, the one which
	 *            has the old maturity equal to null, this is true only if you
	 *            are recomputing the cross overs since beginning
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	private void _computeCutOffsSince(long lowerDate, boolean feedTheFirst)
			throws DFSException {
		Calendar gc = _getCalendarReferringTo(lowerDate);

		Calendar today = Calendar.getInstance();

		MaxMaturityDayCollector maxMatData = null;
		MaxMaturityDayCollector maxMatPrev = null;
		ArrayList<MaxMaturityDayCollector> maxList = new ArrayList<>();

		ArrayList<CrossoverData> crossOvers = new ArrayList<>();

		U.debug_var(332913, "starting to compute the transition times from ",
				lowerDate, " gc is ", gc.getTime(), " TZ ", gc.getTimeZone()
						.getDisplayName());

		OUTMOST_LOOP: while (gc.getTimeInMillis() < today.getTimeInMillis()) {

			_checkCoherenceOfCrossOversSoFar(crossOvers);

			maxMatData = _getMaximumDailyMaturityFor_New(gc);

			U.debug_var(291913, "max for ", gc.getTime(), " is ", maxMatData);
			gc.add(Calendar.DAY_OF_MONTH, 1);
			if (maxMatData == null) {
				continue;
			}

			maxList.add(maxMatData);

			if (maxMatPrev != null) {
				if (maxMatData.changedMaturity(maxMatPrev)) {
					U.debug_var(199912, "Change, from ", maxMatPrev, " to ",
							maxMatData);
					/*
					 * Now, the searching for the corresponding back is a bit
					 * tricky, because we know that yesterday there has been a
					 * crossover from m1 to m2, that means that yesterday vol1 >
					 * vol2, and the day before vol2 was maximum by definition.
					 * 
					 * The crossover is yesterday and so we have to find the
					 * difference between this new winner and the second one IF
					 * the second one is actually m2, so the prev!
					 */
					MaxMaturity mm;
					mm = maxMatData.searchInLosers(maxMatPrev);

					int priceOffset;
					long crossOverDate;
					if (mm != null) {

						// price offset is winner - loser and I have found the
						// winner here
						priceOffset = maxMatData.maxMaturities.get(0).closePrice
								- mm.closePrice;
						crossOverDate = gc.getTimeInMillis();
					} else {

						U.debug_var(192934, "Cannot find ", maxMatPrev,
								" in losers, so I search ", maxMatData,
								" in the losers of the past.");

						MaxMaturityDayCollector item = null;
						for (int i = maxList.size() - 2; i >= 0; --i) {
							item = maxList.get(i);
							U.debug_var(819329, "Considering item ", item);
							mm = item.searchInLosers(maxMatData);
							if (mm != null) {
								U.debug_var(193910, "won ", item,
										" with item ", item);
								break;
							}
						}

						if (mm == null || item == null) {
							// I give up
							U.debug_var(391098,
									"Cannot find a sensible cut off for ",
									maxMatData, " I reset the list");
							crossOvers.clear();
							maxList.clear();
							maxList.add(maxMatData);
							// this is the first winner
							CrossoverData cd = new CrossoverData(
									gc.getTimeInMillis(),
									null,
									_data.get(maxMatData.maxMaturities.get(0).maturity),
									0);
							crossOvers.add(cd);
							maxMatPrev = maxMatData;
							continue;
						}

						// Ok, if I have found the data I can compute the price
						// offset that in this case is different

						// winner - loser, so it's the opposite
						priceOffset = mm.closePrice
								- item.maxMaturities.get(0).closePrice;
						/*
						 * Because the xover date happens in the next day, or
						 * better, at the end of $item's period.
						 */
						crossOverDate = item.date.getTime() + Yadc.ONE_DAY_MSEC;

						/*
						 * This is a theoretical cross date, because there may
						 * be already one (or maybe more) crosses at a date
						 * which is less than this.
						 * 
						 * The algorithm should check that:
						 * 
						 * 1. all the crosses are before the computed cross
						 * date.
						 * 
						 * 2. If one or more xovers are after the computed cross
						 * date they should be discarded and the losers
						 * recomputed (because the old maturity may have been
						 * deleted).
						 */
						while (crossOverDate <= crossOvers.get(crossOvers
								.size() - 1).crossDate) {
							/*
							 * Ok, the date is lower, so I have to delete the
							 * previous cross
							 */
							CrossoverData lastCross = crossOvers.get(crossOvers
									.size() - 1);
							// of course the last cross must be compatible
							// assert (lastCross.newMaturity.getMaturity() ==
							// maxMatData.maxMaturities
							// .get(0).maturity);
							U.debug_var(191939,
									"removing last crossing cross ", lastCross,
									" which overlaps with ", new Date(
											crossOverDate));
							crossOvers.remove(crossOvers.size() - 1);
							/*
							 * Now I have to make a glue with the last-1 cross,
							 * if there is one, otherwise I will reset the list,
							 * it is not really safe to continue
							 */
							if (crossOvers.size() == 0) {
								/*
								 * no cross left, this is the last one, so I can
								 * reset the price offset and the old maturity
								 */
								U.debug_var(
										391098,
										"after removing last the list is empty, this returns the first x over ",
										item);
								crossOvers.clear();
								maxList.clear();
								maxList.add(maxMatData);
								// this is the first winner
								CrossoverData cd = new CrossoverData(
										gc.getTimeInMillis(), null,
										_data.get(maxMatData.maxMaturities
												.get(0).maturity), 0);
								crossOvers.add(cd);
								maxMatPrev = maxMatData;
								continue OUTMOST_LOOP;
							}
							/*
							 * Ok the list is now not empty, I take the last
							 * crossover and try to make a glue with this item.
							 */
							lastCross = crossOvers.get(crossOvers.size() - 1);
							U.debug_var(193819,
									"the last cross has now become ", lastCross);

							/*
							 * I have to find the new maturity in the losers of
							 * this last cross, curiosly the date to find is one
							 * day before, because the x/over is one day after.
							 */
							Calendar gc_tofind = _getCalendarReferringTo(lastCross.crossDate);
							gc_tofind.add(Calendar.DAY_OF_MONTH, -1);
							boolean found = false;
							for (int i = maxList.size() - 1; i >= 0; --i) {
								item = maxList.get(i);
								if (item.date.getTime() != gc_tofind
										.getTimeInMillis()) {
									continue;
								}
								found = true;
								U.debug_var(819329,
										"found the max collector at date ",
										lastCross);

								MaxMaturity loser = maxMatData
										.searchInLosers(item);
								if (loser == null) {
									U.debug_var(193993, "Cannot find ",
											maxMatData.maxMaturities.get(0),
											" in item, I reset the list");
									crossOvers.clear();
									maxList.clear();
									maxList.add(maxMatData);
									// this is the first winner
									CrossoverData cd = new CrossoverData(
											gc.getTimeInMillis(), null,
											_data.get(maxMatData.maxMaturities
													.get(0).maturity), 0);
									crossOvers.add(cd);
									maxMatPrev = maxMatData;
									continue OUTMOST_LOOP;
								}
								// loser is not null...
								U.debug_var(193910, "loser ", loser,
										" found, now I adjust the price offset");
								// winner - loser
								priceOffset = item.maxMaturities.get(0).closePrice
										- loser.closePrice;

								/*
								 * I have found a new practical glue. This means
								 * that I have to update the maxMatPrev
								 */
								maxMatPrev = item;
							}

							if (!found) {
								U.debug_var(193993,
										"Cannot find the item at time 	",
										lastCross);
								throw new IllegalStateException(); // help me
							}

						}

					}

					// the data at which the crossover takes place is actually
					// the after bar,
					// because I have the data for the INITIAL START of the day,
					// but I get it
					// on the next midnight.
					// so, for example, if I know that a xover has been created
					// on July 1st, actually
					// I know it on July 2nd, because I receive the July 1st bar
					// on the first minute of
					// July 2nd, gc is already been incremented, so this is not
					// a problem, it is on midnight (for our time zone at 6
					// a.m., usually, except during daylight saving time
					// mismatch).

					/*
					 * In other words the xover date is the INITIAL time for
					 * which the new maturity takes place. A xover date of July
					 * 2nd means that the xover has been on the day before, when
					 * the old maturity was still valid.
					 * 
					 * Another problem is the price offset. The relative price
					 * offset is, by definition, the difference in price between
					 * the new maturity and the last one, but this is the price
					 * offset of the PREVIOUS chunk, because we are putting
					 * initially the relative chunk. If you notice in the other
					 * constructors of the CrossOverData the initial offset is
					 * zero, and this is OK, because at first there is no
					 * winner, that is the first chunk, the old maturity is
					 * null..., so this is OK, BECAUSE we cannot define a
					 * meaningful price offset. For the second and later, the
					 * price offset will be put in the previous chunk.
					 * 
					 * The price offsets will be integrated by the
					 * ContinuousData class.
					 */

					CrossoverData preLastCd = crossOvers
							.get(crossOvers.size() - 1);
					preLastCd.offsetOffset(priceOffset);

					CrossoverData cd = new CrossoverData(
							crossOverDate,
							_data.get(maxMatPrev.maxMaturities.get(0).maturity),
							_data.get(maxMatData.maxMaturities.get(0).maturity),
							0);

					// _contData.newCrossOver(cd);
					crossOvers.add(cd);

					maxMatPrev = maxMatData;
				}
			} else {
				// first winner, unconditionally
				CrossoverData cd = new CrossoverData(gc.getTimeInMillis(),
						null,
						_data.get(maxMatData.maxMaturities.get(0).maturity), 0);
				// _contData.newCrossOver(cd);
				crossOvers.add(cd);
				maxMatPrev = maxMatData;
			}

		}

		if (!feedTheFirst && crossOvers.size() == 1) {
			U.debug_var(193010, "There is not a x over since ", new Date(
					lowerDate), " nothing to do");
			return;
		}

		// the first winner does not count
		if (crossOvers.size() != 0 && !feedTheFirst) {
			crossOvers.remove(0);
		}

		U.debug_var(192913,
				"End of computing transition times, I feed the cross overs.");
		for (CrossoverData item : crossOvers) {
			_contData.newCrossOver(item);
		}

	}

	/**
	 * returns true if one maturity needs recompute, this is usually meant to be
	 * a
	 * 
	 * @return
	 */
	private boolean _doesOneMaturityNeedRecompute() {
		boolean res = false;
		for (MaturityData data : _data.values()) {
			res |= data.needsRecompute();
		}
		return res;
	}

	/**
	 * Creates a calendar which refers to a particular date and it is linked
	 * exactly to the midnight of that particular date.
	 * 
	 * @param lowerDate
	 * @return
	 */
	@SuppressWarnings("boxing")
	private Calendar _getCalendarReferringTo(long lowerDate) {
		/*
		 * This computation is tricky, because the search is exact in the
		 * midnight, but the midnight on the time zone of new york, which could
		 * be the 5 or 6 a.m. here in Italy.
		 * 
		 * As we don't know in which period of the year we are we simply revert
		 * to a calendar in the time zone of NewYork
		 */
		Calendar gc = Calendar.getInstance(TimeZone
				.getTimeZone(this._symbol.timeZone));

		Calendar before = Calendar.getInstance(TimeZone
				.getTimeZone(this._symbol.timeZone));
		before.setTimeInMillis(lowerDate);

		U.debug_var(919391, "First of set time cal is ", gc.getTime(),
				" before is ", before.getTime(), " y ",
				before.get(Calendar.YEAR), " m ", before.get(Calendar.MONTH),
				" d ", before.get(Calendar.DAY_OF_MONTH));

		gc.clear();
		/*
		 * 0,0,0 is the midnight of the time zone of that calendar, which is New
		 * York. This is OK, because in the daily table we have only bars with
		 * time always midnight.
		 */
		gc.set(before.get(Calendar.YEAR), before.get(Calendar.MONTH),
				before.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

		U.debug_var(919391, "after set time ", gc.getTime());

		return gc;
	}

	private MaxMaturityDayCollector _getMaximumDailyMaturityFor_New(Calendar gc)
			throws DFSException {
		MaxMaturityDayCollector mmdc = new MaxMaturityDayCollector(gc.getTime());

		for (MaturityData md : _data.values()) {
			Bar bar = md.getTable(BarType.DAILY).getBarStartingAtTime(
					gc.getTimeInMillis());

			if (bar != null) {
				mmdc.accept(md.getMaturity(), bar.getClose(), bar.getVolume());
			}
		}

		if (mmdc.maxMaturities.size() == 0) {
			return null;
		}

		return mmdc;
	}

	/**
	 * returns the minimum daily bar date for all the maturities in this symbol.
	 * 
	 * <p>
	 * It is used as the starting point for the cut off computation.
	 * 
	 * @return the minimum date for which there is a daily bar defined
	 * @throws DFSException
	 */
	private long _getMinimumDailyBarDate() throws DFSException {
		long minimum = Long.MAX_VALUE;
		for (MaturityData item : _data.values()) {
			SingleWidthTable table = item.getTable(BarType.DAILY);
			if (table.size() == 0)
				continue;
			long startingTime = table.getStartingTime();
			minimum = Math.min(minimum, startingTime);
		}
		return minimum;
	}

	public void close() {
		// I pass the messages to all the maturities.

		for (MaturityData md : _data.values()) {
			md.close();
		}
	}

	/**
	 * This is the "normal" method to do the finite state machine.
	 * 
	 * <p>
	 * The method is used to perform the state filling procedure for the tables
	 * inside this symbol.
	 * 
	 * <p>
	 * This method will handle differently the initial starting up of the symbol
	 * because it has changed, the initial download is triggered by the manual
	 * or automatic scheduling and it is not done until then, so the symbol does
	 * not start the download until the scheduler fires.
	 * 
	 * @param aFeed
	 * @throws IOException
	 *             if something goes wrong!!!!
	 * @throws DFSException
	 */
	public void doOneStep(IDataFeed aFeed, boolean isFromScheduler)
			throws IOException, DFSException {

		/*
		 * A simple consistency check: this will be useless after a while,
		 * because I have written the initialize method, but for some time
		 * please remain this, after that you could remove also the states from
		 * the switch below
		 */

		switch (_state) {
		case ABORT:
			return;
		case BLANK:
		case COLLECT_CURRENT_MATURITY:
		case COLLECT_FUTURE_MATURITIES:
		case COLLECT_PAST_MATURITIES:
			// case COMPUTE_CUT_OFFS:
			throw new IllegalStateException(getSymbol() + " invalid state "
					+ _state + " in doOneStep");
		case READY:
			break;
		case COMPUTE_CUT_OFFS:
			_computeCutOffsComplete();
			_state = EState.READY;
			return;
		default:
			break;

		}

		// Only for debug this part, it will compute all the cut offs
		boolean computeCutOffNew = false;
		if (computeCutOffNew) {
			_computeCutOffsNew();
		}

		boolean test = false;
		if (test) {
			_contData.endCrossOverComputation();
		}

		for (MaturityData md : _data.values()) {
			md.doOneStep(this, aFeed, isFromScheduler);
		}

		if (_state == EState.READY) {
			_contData.doOneStep(this, aFeed, isFromScheduler);
			if (_contData.needsRecompute() || _doesOneMaturityNeedRecompute()) {
				debug_var(391934, _symbol,
						" I will recompute the cut offs, please wait.");
				_computeCutOffsComplete();
			}

			if (Yadc.isOneCalendarDayPassed(_lastMaturityCheck)) {
				_checkCrossOver();
				_lastMaturityCheck = System.currentTimeMillis();
			}

			// just make sure that the last maturity is not empty.
			MaturityData md = _data.lastEntry().getValue();
			if (md.isReady()) {
				if (!md.isEmpty()) {
					Maturity next = _data.lastKey().getNext();
					md = new MaturityData(_symbol, next);
					_data.put(next, md);
					debug_var(389193, "Added the maturity ", next,
							" to the maturities");
				}
			}

		} else {
			U.debug_var(819293, "symbol ", this.getSymbol(), " in state ",
					_state, " nothing to do");
		}

	}

	@Override
	public int getBarCount(Maturity parsedMaturity, BarType aType, int barWidth)
			throws DFSException {
		if (parsedMaturity == null) {
			return _contData.getBarCount(aType, barWidth);
		}
		return _data.get(parsedMaturity).getBarCount(aType, barWidth);
	}

	/**
	 * gets the number of bars between a certain
	 * 
	 * @param parsedMaturity
	 * @param aType
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws DFSException
	 */
	@Override
	public int getBarsBetween(Maturity parsedMaturity, BarType aType,
			int barWidth, long startDate, long endDate) throws DFSException {
		if (parsedMaturity == null) {
			return _contData
					.getBarsBetween(aType, barWidth, startDate, endDate);
		}
		if (_data.get(parsedMaturity) == null) {
			throw new DfsEmptyDatabaseException("The maturity "
					+ parsedMaturity.toDataProviderLongString()
					+ " has no data");
		}

		return _data.get(parsedMaturity).getBarsBetween(aType, barWidth,
				startDate, endDate);
	}

	public IBarCache getCache(Maturity aMaturity, BarType aType, int nUnits)
			throws DFSException {
		if (aMaturity == null) {
			return _contData.getCache(aType, nUnits);
		}
		return _data.get(aMaturity).getCache(aType, nUnits);
	}

	public ContinuousData getContData() {
		return _contData;
	}

	/**
	 * returns the current symbol, that is the symbol which is used to make the
	 * continuous contract now
	 * 
	 * @return
	 * @throws DFSException
	 */
	@Override
	public String getCurrentSymbol() throws DFSException {

		return _symbol.prefix
				+ _contData.getCurrentMaturity().toDataProviderMediumString();
	}

	@Override
	public long getDateAfterXBarsFrom(Maturity parsedMaturity, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		if (parsedMaturity == null) {
			return _contData.getDateAfterXBarsFrom(aType, barWidth, startDate,
					numBars);
		}
		return _data.get(parsedMaturity).getDateAfterXBarsFrom(aType, barWidth,
				startDate, numBars);
	}

	@Override
	public long getDateBeforeXBarsFrom(Maturity parsedMaturity, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {

		if (parsedMaturity == null) {
			return _contData.getDateBeforeXBarsFrom(aType, barWidth, endTime,
					numBars);
		}
		return _data.get(parsedMaturity).getDateBeforeXBarsFrom(aType,
				barWidth, endTime, numBars);
	}

	/**
	 * builds a symbol status based on all the maturities which are present in
	 * this symbol.
	 * 
	 * @return a snapshot of the state for this symbol.
	 * @throws DFSException
	 * 
	 */
	@Override
	public DfsSymbolStatus getStatus(boolean forceCheck) throws DFSException {
		DfsMaturitySymbolStatus dfs = new DfsMaturitySymbolStatus(this._symbol);
		for (MaturityData md : _data.values()) {
			MaturityStats ms = md.getStatus(forceCheck);
			dfs.maturityStats.add(ms);
		}

		dfs.continuousStats = _contData.getStatus(forceCheck);

		return dfs;
	}

	/**
	 * initializes the symbol, this method is only called when the symbol is
	 * added for the first time
	 * 
	 * @param aFeed
	 *            the data feed, it should be already connected.
	 * @throws IOException
	 *             if something bad occurs.
	 * @throws DFSException
	 */
	public void initialize(IDataFeed aFeed) throws IOException, DFSException {
		switch (_state) {
		case BLANK:
			// //Ok, I have to collect the current maturity, so I simply create
			// the MaturityData object
			Maturity tentativeCurrentMaturity = new Maturity();
			MaturityData md = new MaturityData(_symbol,
					tentativeCurrentMaturity);
			_data.put(tentativeCurrentMaturity, md);
			md.doOneStep(this, aFeed, true);

			// The current maturity is not ready, it will be ready after the
			// class has made the
			// continous contract analysis.
			_state = EState.COLLECT_CURRENT_MATURITY;
			break;
		case COLLECT_CURRENT_MATURITY:
			// In this state I wait for the current maturity to be filled.
			assert (_data.size() == 1); // I have only the current maturity
										// filling
			if (_data.firstEntry().getValue().isReady()) {
				// Ok, data is ready, I could go to the other maturities.
				Maturity firstMat = _data.firstKey();
				// I go into the past...
				Maturity prev = firstMat.getPrevious();
				debug_var(372923, "Trying to get the maturity ", prev);

				md = new MaturityData(_symbol, prev);
				_data.put(prev, md);
				md.doOneStep(this, aFeed, true);
				_state = EState.COLLECT_PAST_MATURITIES;

			} else {
				debug_var(
						183919,
						"Symbol Data, I have to wait here, because the maturity ",
						_data.firstEntry().getValue(), " is not ready yet!");
			}

			break;
		case COLLECT_PAST_MATURITIES:
			debug_var(391051, "Waiting for the past maturity to be ready...",
					_data.firstKey());

			md = _data.firstEntry().getValue();
			if (md.isReady()) {

				boolean toRemove = false;
				if (md.isEmpty() && md.getMaturity().isAQuarterMaturity()) {
					debug_var(382935,
							"maturity is empty, so we have reached the bottom ");
					_state = EState.COLLECT_FUTURE_MATURITIES;
					break;
				} else if (md.isEmpty()
						&& !md.getMaturity().isAQuarterMaturity()) {
					debug_var(292110,
							"maturity is empty but it is a monthly maturity, I delete it and go back again");
					/*
					 * I remove it after because I take the previous, and this
					 * is the first key, otherwise I will end to add the same
					 * maturity forever (removing the first, and then
					 * recomputing the same first)
					 */
					toRemove = true;
				} else {
					debug_var(810391, "Maturity ", _data.firstKey(),
							" is ready, let's try the previous");
				}

				Maturity prev = _data.firstKey().getPrevious();

				if (toRemove) {
					md.removeFromDisk();
					_data.remove(md.getMaturity());
				}

				md = new MaturityData(_symbol, prev);
				md.doOneStep(this, aFeed, true);
				_data.put(prev, md);

			}

			break;
		case COLLECT_FUTURE_MATURITIES:

			debug_var(739894, "collecting future maturites, last is ",
					_data.lastKey());

			md = _data.lastEntry().getValue();
			if (md.isReady()) {
				boolean toRemove = false;
				if (md.isEmpty() && md.getMaturity().isAQuarterMaturity()) {

					if (_allEmptyMaturities()) {
						debug_var(389291, this._symbol,
								" empty! probably it is invalid... ");
						_state = EState.ABORT;
						break;
					}

					debug_var(
							238938,
							this._symbol,
							" Maturity is empty, I have finished to collect future maturities, now the cutoff");
					_state = EState.COMPUTE_CUT_OFFS;
					/*
					 * I do not compute the cut offs, because if the algorithm
					 * crashes I have to redo all the data input. I prefer in
					 * this version to compute the cutoffs in the next stage.
					 */
					// _computeCutOffsComplete();
					break;
				} else if (md.isEmpty()
						&& !md.getMaturity().isAQuarterMaturity()) {
					debug_var(238938, this._symbol, " Maturity ",
							md.getMaturity(),
							" is empty, but monthly, I will take next and this will be removed");
					toRemove = true;

				}

				Maturity next = _data.lastKey().getNext();
				if (toRemove) {
					md.removeFromDisk();
					_data.remove(md.getMaturity());
				}
				md = new MaturityData(_symbol, next);
				md.doOneStep(this, aFeed, true);
				_data.put(next, md);
			}
			break;
		case READY:
			throw new IllegalStateException();
		case ABORT:
			throw new IllegalStateException("Symbol is aborted");
		case COMPUTE_CUT_OFFS:
			throw new IllegalStateException("the cut off state cannot be here");
		default:
			break;
		}

	}

	public boolean isReady() {
		switch (_state) {
		case ABORT:
			/*
			 * abort is "ready", in a certain sense. It is a blocked state
			 */
			return true;
		case BLANK:
		case COLLECT_CURRENT_MATURITY:
		case COLLECT_FUTURE_MATURITIES:
		case COLLECT_PAST_MATURITIES:
			return false;
		case READY:
		case COMPUTE_CUT_OFFS:
			return true;

		default:
			throw new IllegalStateException();

		}
	}

	protected Object readResolve() {
		if (_timeZone == null || _timeZone.length() == 0) {
			_timeZone = DEFAULT_TIME_ZONE;
		}
		/*
		 * This is an emergency switch and it is used only if the program
		 * crashed during the cut off computation.
		 */
		if (_state == EState.COMPUTE_CUT_OFFS) {
			_state = EState.READY;
		}
		return this;
	}

	@Override
	public IBarCache returnCache(Maturity parsedMaturity, RequestParams aReq)
			throws DFSException {
		if (parsedMaturity == null) {
			return _contData.getCache(aReq);
		}
		return _data.get(parsedMaturity).getCache(aReq);
	}

	@Override
	public void truncateMaturity(Maturity parsedMaturity, BarType aType,
			long truncateDate) throws DFSException {
		_data.get(parsedMaturity).truncate(aType, truncateDate);
	}

	@Override
	public void watchMaturity(Maturity parsedMaturity,
			IDatabaseChangeListener aListener) {
		if (parsedMaturity == null) {
			_contData.watch(aListener);
		} else {
			_data.get(parsedMaturity).watch(aListener);
		}

	}

	@Override
	public void unwatchMaturity(Maturity parsedMaturity) {
		if (parsedMaturity == null) {
			_contData.unwatch();
		} else {
			_data.get(parsedMaturity).unwatch();
		}
	}
}
