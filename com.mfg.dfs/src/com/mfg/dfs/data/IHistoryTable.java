package com.mfg.dfs.data;

import java.io.Serializable;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.dfs.misc.IDataFeed;

/**
 * an interface used to make a common ancestor for the {@linkplain HistoryTable}
 * and the continous history table.
 * 
 * <p>
 * The continuous history table is a continuous view of the data, but it will in
 * any case implement this same interface.
 * 
 * @author Sergio
 * 
 */
public interface IHistoryTable extends Serializable {

	/**
	 * closes the given cache.
	 * <p>
	 * Usually this cache is bound to a {@linkplain SingleWidthTable} but
	 * sometimes not.
	 * 
	 * @param aCache
	 *            the cache which you want to close.
	 */
	public void closeCache(IBarCache aCache);

	/**
	 * does one step to update the table.
	 * 
	 * 
	 * @param aSymbolData
	 * @param aFeed
	 * @param isFromScheduler
	 * @return true if something is changed. If isFromScheduler is true the
	 *         return is false, even if the scheduler will change something.
	 * @throws DFSException
	 */
	public boolean doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws DFSException;

	/**
	 * gets a bar with a particular index.
	 * 
	 * @param aIdx
	 * @return
	 * @throws DFSException
	 */
	public Bar getBarAbs(int aIdx) throws DFSException;

	/**
	 * gets the bar at a particular time in the database: the search is exact.
	 * 
	 * <p>
	 * This is mostly done for daily and minute bars which have a fixed time.
	 * Probably it has not much meaning for the range table, because the range
	 * bars do not have a fixed, regular time interval.
	 * 
	 * @param time
	 *            the time in millis
	 * @return the bar if it is present, null otherwise.
	 * @throws DFSException
	 */
	public Bar getBarStartingAtTime(long time) throws DFSException;

	/**
	 * returns the number of bars between a start and end date.
	 * 
	 * <p>
	 * The start date is inclusive and the end date is exclusive.
	 * 
	 * <p>
	 * For daily and minute bars the duration is fixed, so this is not really a
	 * problem
	 * 
	 * <p>
	 * The end date is the instant of the last bar, and this is defined also for
	 * the range bars, the ending time of the last bar, ironically, is the
	 * starting time of the not existing bar, that is is the first instant for
	 * which we have not data.
	 * 
	 * <p>
	 * For example in case of minute bars, if the last bar has time 14:05 it
	 * means that we have data until 14:04:59.999 because the 14:05 bar has data
	 * from 14:04:00.000 to 14:04:59.999.
	 * 
	 * <p>
	 * But to avoid returning an ending time in this manner, the convention
	 * followed by all the query methods is that the starting time is inclusive
	 * and the ending time is exclusive, that is [start,end).
	 * <p>
	 * By definition we have that
	 * 
	 * <p>
	 * <code>getBarsBetween(getStartingDate(), getEndingDate()) == size()</code>
	 * *
	 * <p>
	 * and
	 * <p>
	 * <code>getBarsBetween(t,t) == 0</code> for every t inside the range
	 * [start, end), that is the method returns the number of <b>complete</b>
	 * bars inside the range, if a bar is not completed it will not be counted.
	 * 
	 * <p>
	 * these is the highest interval for which the method gives an answer,
	 * otherwise it throws an exception signaling that the range is out of
	 * bounds.
	 * 
	 * <p>
	 * If the size of the table is 2, for example, and this is a minute table,
	 * the starting date could be 14:04:00 and the ending time could be
	 * 14:06:00. The behaviour of the method in the various cases are
	 * 
	 * <p>
	 * starting time less than 14:04:00 exception
	 * <p>
	 * starting > ending time exception
	 * <p>
	 * ending > 14:06:00 exception
	 * <p>
	 * starting == 14:04:00 AND ending time == 14:06:00 returns 2
	 * <p>
	 * 14:04:00 &lt starting &lt 14:05:00 AND ending time == 14:06:00 returns 1
	 * <p>
	 * starting == 14:04:00 AND 14:05:00 &lt ending &lt 14:06:00 returns 1
	 * 
	 * <p>
	 * any other case returns 0
	 * 
	 * <p>
	 * This because the algorithm returns the complete bars between two times.
	 * 
	 * <p>
	 * For example if startDate is 14:05:33, the first bar counted is the
	 * 14:07:00 bar, which starts at 14:06:00, which is the first available
	 * complete minute.
	 * 
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the number of <b>complete</b> bars inside the specified interval.
	 *         A bar which starts after the start or ends after the end date is
	 *         not counted. A bar which ends exactly one millisecond before the
	 *         end date is counted, this is the case for daily and minute bars,
	 *         for example the bar which ends at 14.05.59.999 is returned if the
	 *         end date is 14.06.00.000
	 * @throws DFSException
	 *             if anything is wrong
	 */
	public int getBarsBetween(long startDate, long endDate) throws DFSException;

	/**
	 * returns the date after a certain number of bars, with the start time
	 * fixed.
	 * 
	 * <p>
	 * The startDate must be not smaller than {@link #getStartingTime()}, and
	 * the maximum number of bars accepted is {@link #size()}, in this case
	 * {@link #getEndingTime()} is returned.
	 * 
	 * @param startDate
	 *            the starting instant to count from
	 * @param numBars
	 *            how many <b>complete</b> bars are requested
	 * @return the <b>ending</b> time of bar requested.
	 * @throws DFSException
	 *             if anything goes wrong
	 */
	public long getDateAfterXBarsFrom(long startDate, int numBars)
			throws DFSException;

	/**
	 * returns the date before a certain number of bars, with the end time
	 * fixed.
	 * 
	 * <p>
	 * The endTime must be not bigger than {@link #getEndingTime()}, and the
	 * maximum number of bars accepted is {@link #size()}, in this case
	 * {@link #getStartingTime()} is returned.
	 * 
	 * @param endTime
	 * @param numBars
	 * @return the <b>starting</b> time of bar requested.
	 * @throws DFSException
	 */
	public long getDateBeforeXBarsFrom(long endTime, int numBars)
			throws DFSException;

	/**
	 * The ending time is the last time for which this table has data.
	 * 
	 * <p>
	 * The convention used is that each table has a left closed, right open
	 * interval in which there is data, that is [start, end)
	 * 
	 * <p>
	 * This is equivalent to asking {@link #getEndingTimeOfBarAt(int)} at
	 * {@link #size()} -1
	 * 
	 * @return
	 * @throws DFSException
	 */
	public long getEndingTime() throws DFSException;

	/**
	 * returns the ending time for the bar at a particular index.
	 * 
	 * @param aIndex
	 * @return its ending time
	 * @throws DFSException
	 */
	public long getEndingTimeOfBarAt(int aIndex) throws DFSException;

	/**
	 * builds the statistics for this table.
	 * 
	 * @param forceCheck
	 *            if true the table will do also a coherence check.
	 */
	public DfsIntervalStats getStats(boolean forceCheck);

	/**
	 * returns a simple "friendly" name of this history table.
	 * 
	 * @return
	 */
	public String getKey();

	public Maturity getMaturity();

	/**
	 * returns the rightmost bar whose ending time is less than time, that is
	 * the maximum index in the table where the bar is entirely contained in
	 * time, with its ending time less or equal, at most, to the time given.
	 * 
	 * <p>
	 * If a bar cannot be found it returns -1, this means that all the bars are
	 * over cDate, this is returned also if the starting time of the first bar
	 * is before cDate, but the ending time after, because only complete bars
	 * are considered.
	 * 
	 * <p>
	 * If all the bars are within cDate the last bar is returned, that is
	 * size()-1
	 * 
	 * @param cDate
	 *            the date used to search
	 * 
	 * @throws DFSException
	 *             if something wrong happens.
	 * 
	 */
	public int getMaximumIndexOfBarWithin(long cDate) throws DFSException;

	/***
	 * returns the leftmost, minimum, index of a bar wholly contained after
	 * aTime, that is its starting time is after or equal aTime.
	 * 
	 * <p>
	 * In cache philosophy this may be translated as the floor of the starting
	 * time, but in the table we consider the bars as units with a time
	 * interval.
	 * 
	 * @param aTime
	 *            the time to find
	 * @return the minimum index, that is the leftmost index of a bar after
	 *         aTime, or -size() if this bar is not found.
	 * @throws DFSException
	 */
	public int getMinimumIndexOfBarAfter(long aTime) throws DFSException;

	/**
	 * returns the starting time of this table, when the table starts to have
	 * data.
	 * 
	 * <p>
	 * The concept of starting time is easy for daily and minute tables, but it
	 * is not so easy for range ones, because the first bar does not have a real
	 * starting time (this could change in the future, but for now it is so).
	 * 
	 * <p>
	 * The time returned is the time after that we have data for this particular
	 * table and type, which is <b>not</b> always equal to the time of the first
	 * bar (only in case of daily bars it is so).
	 * 
	 * <p>
	 * This time can be used to get the bars between two dates.
	 * 
	 * 
	 * 
	 * @return
	 * @throws DFSException
	 */
	public long getStartingTime() throws DFSException;

	/**
	 * returns the starting time for the bar at a particular index
	 * 
	 * @param aIndex
	 * @return the starting time
	 * @throws DFSException
	 */
	public long getStartingTimeOfBarAt(int aIndex) throws DFSException;

	public DfsSymbol getSymbol();

	public BarType getType();

	public boolean isEmpty();

	/**
	 * returns the size of this history table.
	 * 
	 * @return the size, which is simply <code>{@link #upLimit()} + 1</code>
	 */
	public int size();

	/**
	 * 
	 * @return the upper limit of the table, that is the highest integer that
	 *         the user can use in the method {@link #getBarAbs(int)}
	 */
	public int upLimit();

}
