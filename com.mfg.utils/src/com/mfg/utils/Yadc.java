package com.mfg.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Yet another date class.
 * 
 * <p>
 * The name says it all. It is just a utility class to simplify certain date
 * computations.
 * 
 * @author Sergio
 * 
 */
public final class Yadc {

	/**
	 * How many milliseconds in one second
	 */
	public static final long ONE_SECOND_MSEC = 1000;

	/**
	 * How many milliseconds in one minute
	 */
	public static final long ONE_MINUTE_MSEC = ONE_SECOND_MSEC * 60;

	/**
	 * How many milliseconds in one hour
	 */
	public static final long ONE_HOUR_MSEC = 60 * ONE_MINUTE_MSEC;

	/**
	 * How many milliseconds in one day
	 */
	public static final long ONE_DAY_MSEC = 24 * ONE_HOUR_MSEC;

	/**
	 * How many milliseconds in half day
	 */
	public static final long HALF_DAY_MSEC = ONE_DAY_MSEC / 2;

	/**
	 * How many milliseconds in one week
	 */
	public static final long ONE_WEEK_MSEC = 7 * ONE_DAY_MSEC;

	/**
	 * How many milliseconds in one month
	 */
	public static final long ONE_MONTH_MSEC = 7 * ONE_WEEK_MSEC;

	/**
	 * How many milliseconds in one year
	 */
	public static final long ONE_YEAR_MSEC = ONE_DAY_MSEC * 365;

	/**
	 * returns true if one day (astronomical, 24 hours) has changed since last
	 * maturity check.
	 * 
	 * @param when
	 *            an instant in time
	 * 
	 * @return true if and only if one complete day (24 hours) have passed. This
	 *         method does not count the calendar, for example from 23 to 1 only
	 *         two hours have passed but the day has switched, this method will
	 *         not care about that.
	 */
	public static boolean isOneDaySince(long when) {
		long now = System.currentTimeMillis();
		return isADayBetween(when, now);

	}

	/**
	 * compares two dates and returns true if 24 hours are passed from the first
	 * to the second.
	 * 
	 * <p>
	 * Probably this method does not handle well daylight changes. Maybe it will
	 * switch after 23 or 25 hours, depending on the change.
	 * 
	 * @param past
	 * @param future
	 * @return
	 */
	public static boolean isADayBetween(long past, long future) {
		if (future - past > ONE_DAY_MSEC) {
			return true;
		}
		return false;
	}

	/**
	 * returns true if one calendar day is passed since a past date.
	 * <p>
	 * 
	 * @param past
	 * @return
	 */
	public static boolean isOneCalendarDayPassed(long past) {
		return isOneCalendarDayPassed(past, System.currentTimeMillis());
	}

	public static boolean isOneCalendarDayPassed(long past, long future) {
		Calendar pastC = new GregorianCalendar();
		Calendar futureC = new GregorianCalendar();
		pastC.setTimeInMillis(past);
		futureC.setTimeInMillis(future);
		return isOneCalendarDayPassed(pastC, futureC);
	}

	public static boolean isOneCalendarDayPassed(Calendar pastC,
			Calendar futureC) {
		if (futureC.compareTo(pastC) <= 0) {
			return false; // we have to be in the future!
		}

		if (futureC.get(Calendar.YEAR) > pastC.get(Calendar.YEAR)) {
			return true;
		}

		if (futureC.get(Calendar.DAY_OF_YEAR) > pastC.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}

	/**
	 * returns the time in the past whose difference with now is exactly n
	 * calendar days.
	 * 
	 * @param days
	 *            the number of days to consider
	 * @return the time in the past
	 * 
	 * @throws IllegalArgumentException
	 *             if days is negative.
	 */
	public static long getNCalendarDaysBeforeNow(int days) {
		if (days < 0) {
			throw new IllegalArgumentException();
		}
		long now = System.currentTimeMillis();
		return now - (days * ONE_DAY_MSEC);
	}

	public static long daysDiff(Date from, Date to) {
		return daysDiff(from.getTime(), to.getTime());
	}

	/**
	 * Computes the difference in days between a from and a to date.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static long daysDiff(long from, long to) {
		return Math.round((to - from) / ((double) ONE_DAY_MSEC));
	}

}
