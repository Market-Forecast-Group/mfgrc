package com.mfg.dfs.misc;

import java.util.Date;
import java.util.GregorianCalendar;

import com.mfg.common.BarType;
import com.mfg.common.Maturity;
import com.mfg.utils.IndexedRandomTickSource;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * A maturity which is able to simulate a start and an end for a particular
 * maturity
 * 
 * <p>
 * This simulated maturity, as the linked class {@linkplain Maturity} is
 * immutable and it simply gives some facts about the matutiry related to the
 * simulated feed which we want to create.
 * 
 * <p>
 * The offset is computed randomly.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class SimulatedMaturity {

	private static final int MINIMUM_MINUTE_YEAR_ARCHIVE = 2010;

	/**
	 * How many days the maturity (simulated) will be alive until its
	 * expiration, after the cut off.
	 * <p>
	 * This information is used to alter the simulated volume of the bar
	 * 
	 */
	private static final long DAYS_BEFORE_EXPIRATION = 4;

	/**
	 * How many tick days are given to the outside. At first this number was 8
	 * to simulate the behavior of iqFeed which gives at most those days of tick
	 * data.
	 * 
	 * <p>
	 * But now we have preferred to make the simulator give all available data,
	 * even if this is not too much coherent with iqFeed. I return 365 which is
	 * 1 year of tick data which should be enough... in any case that number is
	 * only a theoretical limit.
	 */
	private static final int DAYS_OF_RANGE_DATA = 365;

	/**
	 * The <b>real</b> maturity which is underlying this normal maturity.
	 */
	private final Maturity _maturity;

	/**
	 * Creates a simulated maturity which is used to get start/end dates for the
	 * particular bars, and also to adjust the volume of a daily bar, given a
	 * time
	 * 
	 * @param aMaturity
	 *            a <em>real</em> maturity. It is immutable, and also this class
	 *            is immutable.
	 */
	public SimulatedMaturity(Maturity aMaturity) {
		_maturity = aMaturity;
	}

	/**
	 * Returns a volume for a daily bar adjusted in order to simulate a neat
	 * crossover some days before the maturity expiration.
	 * 
	 * I simply simulate a staircase in the volume, to have the crossover always
	 * at the same date.
	 * 
	 * @param aDate
	 * @return
	 */
	public int getAdjustedDailyVolume(long aDate) {

		// to do here. after cut off of next maturity return 10
		long nextMaturityCutOff = this._maturity.getExpirationDate().getTime()
				- Yadc.ONE_DAY_MSEC * DAYS_BEFORE_EXPIRATION;

		if (aDate > nextMaturityCutOff) {
			return 10;
		}

		long extimatedCutOffDate = getEstimateCutOffDate();

		if (aDate >= extimatedCutOffDate) {
			return 999;
		}

		return 100;

	}

	private long getEstimateCutOffDate() {
		long expiration = this._maturity.getPreviousAsQuarter()
				.getExpirationDate().getTime();
		return expiration - Yadc.ONE_DAY_MSEC * DAYS_BEFORE_EXPIRATION;
	}

	/**
	 * Gets the offset for this particular maturity. The prices from the
	 * dataseries will be massaged with an offset which is more or less
	 * depending on the approaching date for this maturity.
	 * 
	 * <p>
	 * I have a continuous tick source, the offset is computed statically from
	 * the information of the date and the maturity itself.
	 * 
	 * <p>
	 * The biggest problem is to reproduce the data stream and the only way to
	 * reproduce it is to either have a repeatable random tick source (and this
	 * is the class {@linkplain IndexedRandomTickSource}) or we must have
	 * repeatable offsets.
	 * 
	 * <p>
	 * Of course the offset does <b>not</b> depend on the bar type, because
	 * <b>all</b> the bar types for the particular data source share the same
	 * data.
	 * 
	 * <p>
	 * The offset is measured in ticks, relative, + or -.
	 * 
	 * 
	 * 
	 * @return the adjusted offset for this maturity and this date.
	 */
	public int getAdjustedOffsetInTicks(long date) {
		/*
		 * The daily bar type is the most in the past, so if we reach past it we
		 * do not have any data to show, and no offset, of course.
		 */
		if ((date < getStartDataTime(BarType.DAILY).getTime())
				|| (date > getExpirationTime())) {
			U.debug_var(399132, "you asked ", new Date(date), " but start it ",
					getStartDataTime(BarType.DAILY), " end is ", new Date(
							getExpirationTime()));
			throw new IllegalArgumentException();
		}
		// A simple constant offset.
		return _maturity.getQuarter();
	}

	/**
	 * The expiration time does not depend from the bar type.
	 * 
	 * @return
	 */
	public long getExpirationTime() {
		return this._maturity.getExpirationDate().getTime();
	}

	/**
	 * returns the start time for this maturity, it means the time when we have
	 * available data, it depends on the maturity date and the bar type.
	 * 
	 * <p>
	 * This date is not filtered, this is the theoretical start data, then the
	 * available start data is then filtered using the symbol's simulator
	 * limiter.
	 * 
	 * @param aType
	 *            the bar type.
	 * 
	 * 
	 * @return
	 */
	public Date getStartDataTime(BarType aType) {
		// this is the raw start not counting the temporal limit
		long rawStart;
		switch (aType) {
		case DAILY:
			rawStart = this._maturity.getStartTradingData().getTime();
			break;
		case MINUTE:
			rawStart = new GregorianCalendar(MINIMUM_MINUTE_YEAR_ARCHIVE, 0, 0)
					.getTimeInMillis();
			rawStart = Math.max(rawStart, _maturity.getStartTradingData()
					.getTime());
			break;
		case RANGE:
			rawStart = System.currentTimeMillis() - DAYS_OF_RANGE_DATA
					* Yadc.ONE_DAY_MSEC;
			rawStart = Math.max(rawStart, _maturity.getStartTradingData()
					.getTime());
			break;
		default:
			throw new IllegalArgumentException();
		}

		// Of course if the maturity is in the future we cannot give simulated
		// data.
		rawStart = Math.min(System.currentTimeMillis(), rawStart);

		return new Date(rawStart);
	}

	public Maturity getMaturity() {
		return _maturity;
	}

}
