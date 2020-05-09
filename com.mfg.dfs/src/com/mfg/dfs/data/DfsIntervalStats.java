package com.mfg.dfs.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mfg.utils.U;

/**
 * This class lists the interval statistics for a given contract and a given bar
 * type.
 * <p>
 * It is in direct relationship with the {@linkplain HistoryTable} which holds
 * the data in DFS.
 * 
 * @author Sergio
 * 
 */
public class DfsIntervalStats {

	public enum EVisibleState {
		COMPLETE, UP_TO_DATE, TRUNCATED
	}

	public DfsIntervalStats() {
		this(null); // only for xtream
	}

	/**
	 * simple constructor with a state.
	 * <p>
	 * The state comes from the table, it is here as a read only state, useful
	 * for the GUI to send to the user some visual clues about the table itself.
	 * 
	 * @param aState
	 */
	public DfsIntervalStats(EVisibleState aState) {
		this(aState, -1, Long.MIN_VALUE, Long.MIN_VALUE);
	}

	public DfsIntervalStats(EVisibleState aState, int aNumBars,
			long aStartDate, long aEndDate) {
		numBars = aNumBars;
		startDate = aStartDate;
		endDate = aEndDate;
		state = aState;

		SimpleDateFormat sdf = new SimpleDateFormat(U.NORMAL_DATE_FORMAT);
		startDateStr = sdf.format(new Date(startDate));
		endDateStr = sdf.format(new Date(endDate));
	}

	public final EVisibleState state;

	/**
	 * how many bars are in the table, this is TOTAL.
	 */
	public final int numBars;

	/**
	 * This is the minimum date in the table.
	 */
	public final long startDate;

	public final String startDateStr;
	public final String endDateStr;

	/**
	 * This is the maximum date in the table itself, at the time of receiving.
	 * <p>
	 * This end date is to be considered "mobile" because we cannot
	 * deterministically know when this date will change (DFS is in real time so
	 * the same call in different times can yeld different results).
	 * 
	 */
	public final long endDate;

}
