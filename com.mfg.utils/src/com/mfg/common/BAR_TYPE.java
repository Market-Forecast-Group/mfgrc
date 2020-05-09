package com.mfg.common;

import com.mfg.utils.Yadc;

public enum BAR_TYPE {
	RANGE, PRICE, SECOND, MINUTE, HOUR, DAILY, WEEKLY, MONTHLY;

	public static final String[] ITEMS = { RANGE.name(), PRICE.name(),
			SECOND.name(), MINUTE.name(), HOUR.name(), DAILY.name(),
			WEEKLY.name(), MONTHLY.name() };

	/**
	 * @param aType
	 *            The type of the bar
	 * @param widthBar
	 *            The width of the bar
	 * @return The bar duration in milliseconds, given a type and a width
	 */
	public static long getBarDuration(final BAR_TYPE aType, final int widthBar) {
		switch (aType) {
		case RANGE:
		case PRICE:
			return 0;
		case SECOND:
			return Yadc.ONE_SECOND_MSEC * widthBar;
		case MINUTE:
			return Yadc.ONE_MINUTE_MSEC * widthBar;
		case HOUR:
			return Yadc.ONE_HOUR_MSEC * widthBar;
		case DAILY:
			return Yadc.ONE_DAY_MSEC * widthBar;
		case WEEKLY:
			return Yadc.ONE_WEEK_MSEC * widthBar;
		case MONTHLY:
			return Yadc.ONE_MONTH_MSEC * widthBar;
		}
		throw new IllegalStateException("unknown state");
	}

}
