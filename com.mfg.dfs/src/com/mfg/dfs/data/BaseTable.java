package com.mfg.dfs.data;

import java.util.Date;

import com.mfg.common.DFSException;
import com.mfg.utils.U;

/**
 * The base table is the base class of all the tables in the system.
 * 
 * <p>
 * For now it has in common the code for the statistics.
 * 
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
abstract class BaseTable implements IHistoryTable {

	// @Override
	// public DfsIntervalStats getStats(boolean forceCheck) {
	// return null;
	// }

	static boolean CHECK_MULTIPLE_TABLES = false;

	/**
	 * Just a testing method, it ensures that between startDate and endDate
	 * there are exactly numBars, and if only one milliseconds is added or
	 * subtracted the numBars decrease.
	 * 
	 * @param numBars
	 * @param startDate
	 * @param endDate
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	void _checkStatCoherence(int nb, long startDatePar, long endDatePar)
			throws DFSException {
		int numBars = nb;
		long startDate = startDatePar;
		long endDate = endDatePar;

		int f1 = getBarsBetween(startDate, endDate);
		if (f1 != numBars) {
			U.debug_var(991931, getKey(), " WRONG received ", f1, " expected ",
					numBars);
			assert (false);
		}

		// now checking the coherence of gdaxb and gdbxb methods.
		long firstDate = getDateBeforeXBarsFrom(endDate, numBars);
		if (firstDate != startDate) {
			U.debug_var(718912, getKey(), " WRONG DBXB ", new Date(firstDate),
					" expected ", new Date(startDate));
		}

		long lastDate = getDateAfterXBarsFrom(startDate, numBars);
		if (lastDate != endDate) {
			U.debug_var(718912, getKey(), " WRONG DAXB ", new Date(lastDate),
					" expected ", new Date(endDate));
		}

		numBars = Math.max(0, numBars - 1);
		startDate = Math.min(startDate + 1, endDate);
		f1 = getBarsBetween(startDate, endDate);

		if (f1 != numBars) {
			U.debug_var(991931, getKey(), " WRONG2 received ", f1,
					" expected ", numBars);
			assert (false);
		}

		numBars = Math.max(0, numBars - 1);
		endDate = Math.max(startDate, endDate - 1);
		f1 = getBarsBetween(startDate, endDate);

		if (f1 != numBars) {
			U.debug_var(991931, getKey(), " WRONG3 received ", f1,
					" expected ", numBars);
			assert (false);
		}

		// U.debug_var(439190, getKey(), " stats OK");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7143322812749048008L;

}
