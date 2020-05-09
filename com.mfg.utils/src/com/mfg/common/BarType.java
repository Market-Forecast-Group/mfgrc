package com.mfg.common;

import com.mfg.utils.Yadc;

/**
 * These are the "fundamental bar types". The types which are inside the
 * database.
 * 
 * <p>
 * All other types should be derived by these ones, because we have the
 * possibility to compute bars of different multiplicity, for example we could
 * create 15-minute bars from the 1-minute.
 * 
 * <p>
 * This enumeration only lists the data types which are physically present in
 * the database.
 * 
 * @author Sergio
 * 
 */
public enum BarType {
	DAILY {
		@Override
		public long getDuration() {
			return Yadc.ONE_DAY_MSEC;
		}

		@Override
		public BarAutomaton createBarAutomatonForYourType() {
			return new TimeBarAutomaton(getDuration(), false);
		}
	},
	MINUTE {
		@Override
		public long getDuration() {
			return Yadc.ONE_MINUTE_MSEC;
		}

		@Override
		public BarAutomaton createBarAutomatonForYourType() {
			return new TimeBarAutomaton(getDuration(), true);
		}
	},
	RANGE {
		@Override
		public long getDuration() {
			return Yadc.ONE_SECOND_MSEC;
		}

		@Override
		public BarAutomaton createBarAutomatonForYourType() {
			return null;
		}
	};

	/**
	 * returns the average duration for a bar of this particular type.
	 * 
	 * <p>
	 * A range type will have a default duration of one second, but this is a
	 * simple default value.
	 * 
	 * @return the normal duration of a bar of this type, in milliseconds
	 */
	public abstract long getDuration();

	public abstract BarAutomaton createBarAutomatonForYourType();
}
