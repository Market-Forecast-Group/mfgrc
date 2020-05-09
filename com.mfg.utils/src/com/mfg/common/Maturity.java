package com.mfg.common;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The maturity is a simple class which holds some utilities to handle with
 * maturities.
 * 
 * <p>
 * The maturity has a year and a quarter, because we have now contracts which
 * have quarter maturities.
 * 
 * <p>
 * The representation of this maturity is then dependent on the data provider.
 * for example we have that eSignal has a 4 year digit and instead iQfeed has a
 * 2 year digit
 * 
 * <p>
 * This should not have a great impact in the class itself because it is capable
 * of obtaining the right maturity for the current data provider.
 * 
 * <p>
 * The class is {@linkplain Comparable} because it is stored inside an ordered
 * collection. This collection is the set of all the maturity data which are
 * stored inside the {@linkplain SymbolData} object.
 * 
 * <p>
 * The maturity class is immutable, like a {@linkplain BigInteger} because in
 * this way I can share references around.
 * 
 * @author Sergio
 * 
 */
public class Maturity implements Comparable<Maturity>, Serializable {

	public static final class ParseMaturityAns {
		public Maturity parsedMaturity;
		public String unparsedString;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8549866327377546261L;

	/**
	 * This is the continuous suffix used to distinguish the continuous maturity
	 * from the normal maturity. The special maturity 'null' is the continuous
	 * maturity
	 */
	public static final String CONTINUOUS_SUFFIX = "#mfg";

	/**
	 * This suffix is used to distinguish CSV data, this data is particular
	 * because it has not a maturity and it is not updateable.
	 */
	public static final String CSV_SUFFIX = "#csv";

	/**
	 * This is the cut off date for the months in which there is a transition
	 */
	private static final int CUT_OFF_DAY_MONTH = 16;

	/**
	 * how many days after the start of the next maturity the current maturity
	 * expires.
	 */
	private static final int OFFSET_START_EXPIRATION_IN_DAYS = 4;

	/**
	 * This is simply the array of maturities
	 * 
	 * the maturities codes are
	 * 
	 * <table>
	 * <tr>
	 * <td>January</td>
	 * <td>F</td>
	 * </tr>
	 * <tr>
	 * <td>February</td>
	 * 
	 * <td>G</td>
	 * <tr>
	 * <td>March</td>
	 * <td>H</td>
	 * 
	 * <tr>
	 * <td>April</td>
	 * <td>J</td>
	 * <tr>
	 * <td>May</td>
	 * <td>K</td>
	 * <tr>
	 * <td>June</td>
	 * <td>M</td>
	 * <tr>
	 * <td>July</td>
	 * <td>N</td>
	 * <tr>
	 * <td>August</td>
	 * <td>Q</td>
	 * <tr>
	 * <td>September</td>
	 * <td>U</td>
	 * <tr>
	 * <td>October</td>
	 * <td>V</td>
	 * <tr>
	 * <td>November</td>
	 * <td>X</td>
	 * <tr>
	 * <td>December</td>
	 * <td>Z</td>
	 * 
	 * 
	 * </table>
	 * 
	 * 
	 */
	private static String[] _maturities = { "F", "G", "H", "J", "K", "M", "N",
			"Q", "U", "V", "X", "Z" };

	private static NumberFormat _ourFormat = new DecimalFormat("00");

	/**
	 * returns the current quarter for the given date.
	 * 
	 * <p>
	 * The quarters are numbered as the seasons, but they start one quarter
	 * before. So zero is 1st quarter but it starts from December 21st, 1 is
	 * "summer" and it starts from March 21st, etc...
	 * 
	 * 
	 * @param cal
	 *            a calendar set to the date which we want to parse in quarter.
	 * 
	 * @return the quarter corresponding to the current calendar, the quarter
	 *         can be 4, this is a "virtual" quarter which correspond to the
	 *         first quarter of the NEXT year
	 */
	private static byte getQuarter(Calendar cal) {
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		switch (month) {
		case Calendar.JANUARY:
		case Calendar.FEBRUARY:
			return 0;
		case Calendar.APRIL:
		case Calendar.MAY:
			return 1;
		case Calendar.JULY:
		case Calendar.AUGUST:
			return 2;
		case Calendar.OCTOBER:
		case Calendar.NOVEMBER:
			return 3;
		case Calendar.MARCH:
			if (day <= CUT_OFF_DAY_MONTH) {
				return 0;
			}
			return 1;
		case Calendar.JUNE:
			if (day <= CUT_OFF_DAY_MONTH) {
				return 1;
			}
			return 2;
		case Calendar.SEPTEMBER:
			if (day <= CUT_OFF_DAY_MONTH) {
				return 2;
			}
			return 3;
		case Calendar.DECEMBER:
			if (day <= CUT_OFF_DAY_MONTH) {
				return 3;
			}
			return 4; // the first quarter of the NEXT year!!!!
		}
		throw new IllegalArgumentException("should not happe");
	}

	/**
	 * given a string it parses the last part of the string and returns a
	 * maturity and the substring which is not parsed.
	 * 
	 * <p>
	 * In the case of the continuous contract (the string ends with "#mfg") the
	 * parsed maturity is null.
	 * 
	 * @param aCompleteSymbol
	 * @return
	 * @throws DFSException
	 */
	public static ParseMaturityAns parseMaturity(String aCompleteSymbol)
			throws DFSException {
		ParseMaturityAns pma = new ParseMaturityAns();
		if (aCompleteSymbol.length() < 4) {
			throw new DFSException("cannot parse " + aCompleteSymbol);
		}

		if (aCompleteSymbol.endsWith(CONTINUOUS_SUFFIX)
				|| aCompleteSymbol.endsWith(CSV_SUFFIX)) {
			pma.parsedMaturity = null;
			pma.unparsedString = aCompleteSymbol.substring(0,
					aCompleteSymbol.length() - 4);
			return pma;
		}

		String suffix = aCompleteSymbol.substring(aCompleteSymbol.length() - 3);
		String prefix = aCompleteSymbol.substring(0,
				aCompleteSymbol.length() - 3);

		pma.unparsedString = prefix;
		pma.parsedMaturity = parseMaturitySuffix(suffix);

		return pma;
	}

	/**
	 * takes a suffix, of 3 letters and parses the maturity according to the
	 * rules.
	 * 
	 * @param suffix
	 * @return
	 * @throws DFSException
	 */
	public static Maturity parseMaturitySuffix(String suffix)
			throws DFSException {
		if (suffix.length() != 3) {
			throw new DFSException("not valid string to parse " + suffix);
		}

		Maturity mat = new Maturity();
		// mat._quarter = -99;
		switch (suffix.charAt(0)) {
		/*
		 * private static String[] _maturities = { "F", "G", "H", "J", "K", "M",
		 * "N", "Q", "U", "V", "X", "Z" };
		 */
		case 'F':
			mat._month = Calendar.JANUARY;
			break;
		case 'G':
			mat._month = Calendar.FEBRUARY;
			break;
		case 'H':
			mat._month = Calendar.MARCH;
			break;
		case 'J':
			mat._month = Calendar.APRIL;
			break;
		case 'K':
			mat._month = Calendar.MAY;
			break;
		case 'M':
			mat._month = Calendar.JUNE;
			break;
		case 'N':
			mat._month = Calendar.JULY;
			break;
		case 'Q':
			mat._month = Calendar.AUGUST;
			break;
		case 'U':
			mat._month = Calendar.SEPTEMBER;
			break;
		case 'V':
			mat._month = Calendar.OCTOBER;
			break;
		case 'X':
			mat._month = Calendar.NOVEMBER;
			break;
		case 'Z':
			mat._month = Calendar.DECEMBER;
			break;
		default:
			throw new DFSException("cannot parse quarter char "
					+ suffix.charAt(0));

		}

		// then I parse the year
		try {
			// the year is parsed with a workaround to catch the year 2000
			// problem
			// if the year is under 90 than it is supposed to be in the XXI
			// century,
			// otherwise it is in the XX century.

			mat._year = Integer.parseInt(suffix.substring(suffix.length() - 2));

			if (mat._year < 90) {
				mat._year += 2000;
			} else {
				mat._year += 1900;
			}

		} catch (NumberFormatException e) {
			throw new DFSException(e);
		}

		return mat;

	}

	/**
	 * The year of the maturity is simply an integer.
	 * <p>
	 * We have then methods to compute maturities for the current year, moving
	 * forward or backward.
	 */
	private int _year;

	/**
	 * This is the month of the maturity, it
	 */
	private byte _month;

	public Maturity() {
		GregorianCalendar gc = new GregorianCalendar();
		_init(gc);
	}

	public Maturity(Calendar cal) {
		_init(cal);
	}

	/**
	 * Creates the current maturity, this constructor is actually not really
	 * used, because we have the possibility to build a maturity given a date
	 * and a calendar
	 * 
	 * <p>
	 * This constructor is package private..., you should not use it.
	 */
	public Maturity(int year, byte month) {
		_year = year;
		// _quarter = -99;
		_month = month;
	}

	/**
	 * builds the maturity given this date.
	 * <p>
	 * The date should be in local time coordinate
	 * 
	 * @param date
	 */
	public Maturity(long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		_init(cal);
	}

	/**
	 * The opposite transformation from month to quarter
	 * 
	 * <p>
	 * this is only a convenience method for maturities exact at quarters, it
	 * will complain if the maturity is not at a quarter.
	 * 
	 * @return the quarter (as a number between 0 and 3)
	 */
	private byte _getQuarter() {
		if ((_month + 1) % 3 != 0) {
			throw new IllegalStateException("_month is " + _month
					+ " This is no OK");
		}
		return (byte) (((_month + 1) / 3) - 1); // integer division.
	}

	private String _getMonthString() {
		return _maturities[_month];
	}

	/**
	 * returns the start date as a calendar object
	 * 
	 * @return
	 */
	private Calendar _getStartDateAsQuarter() {
		Calendar gc = new GregorianCalendar();
		gc.clear();
		int massagedYear = _year;
		gc.set(Calendar.DATE, CUT_OFF_DAY_MONTH);
		switch (_month) {
		case 2:
			// March
			gc.set(Calendar.MONTH, Calendar.DECEMBER);
			massagedYear--;
			break;
		case 5:
			// June
			gc.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case 8:
			// September
			gc.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case 11:
			// December
			gc.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		}

		gc.set(Calendar.YEAR, massagedYear);
		gc.set(Calendar.HOUR, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		return gc;
	}

	private void _init(Calendar cal) {
		byte quarter = getQuarter(cal);
		int offset = 0;
		if (quarter == 4) {
			offset = 1; // the offset in the year...
			quarter -= 4;
		}
		_month = (byte) ((quarter + 1) * 3 - 1);
		_year = cal.get(Calendar.YEAR) + offset;
	}

	@Override
	public int compareTo(Maturity other) {
		if (other._year != this._year) {
			return this._year - other._year;
		}
		// Ok, the year is equal, let's see the month
		return this._month - other._month;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Maturity))
			return false;

		Maturity mat = (Maturity) obj;

		if (mat._year != this._year) {
			return false;
		}

		if (mat._month != this._month)
			return false;

		return true;
	}

	/**
	 * returns the expiration date for this maturity. The expiration date is not
	 * theoretical, and it is set some days after the start date of the next
	 * maturity.
	 * 
	 * <p>
	 * As the {@link #getStartDate()} was written before, this method uses the
	 * start date to compute an expiration date, even if it should be the
	 * contrary.
	 * 
	 * 
	 * @return the date of the expiration time of this maturity, based on the
	 *         start date of the next maturity
	 */
	public Date getExpirationDate() {
		Calendar cal = getNextAsQuarter()._getStartDateAsQuarter();

		cal.add(Calendar.DAY_OF_MONTH, OFFSET_START_EXPIRATION_IN_DAYS);
		return cal.getTime();

	}

	/**
	 * returns the next maturity making a roll over of the year.
	 * 
	 * <p>
	 * This method works using quarters, and not months.
	 * 
	 * @return
	 */
	public Maturity getNextAsQuarter() {
		byte nextMat = (byte) (_month + 3);
		int offset = 0;
		if (nextMat > 11) {
			offset = 1;
			nextMat -= 12;
		}
		return new Maturity(_year + offset, nextMat);
	}

	/**
	 * returns the next maturity, counting every month
	 * 
	 * @return the next maturity
	 */
	public Maturity getNext() {
		byte nextMat = (byte) (_month + 1);
		int offset = 0;
		if (nextMat > 11) {
			offset = 1;
			nextMat -= 12;
		}
		return new Maturity(_year + offset, nextMat);
	}

	/**
	 * 
	 * @return the previous maturity making a rollover of the year.
	 * 
	 *         this method uses quarters, like {@link #getNext()}
	 */
	public Maturity getPreviousAsQuarter() {
		byte prevMat = (byte) (_month - 3);
		int offset = 0;
		if (prevMat < 0) {
			offset = -1;
			prevMat += 12;
		}
		return new Maturity(_year + offset, prevMat);
	}

	/**
	 * returns the previous maturity as a quarter.
	 * 
	 * @return the previous maturity.
	 */
	public Maturity getPrevious() {
		byte prevMat = (byte) (_month - 1);
		int offset = 0;
		if (prevMat < 0) {
			offset = -1;
			prevMat += 12;
		}
		return new Maturity(_year + offset, prevMat);
	}

	public byte getQuarter() {
		return _getQuarter();
	}

	/**
	 * gets the start date of this maturity, the start date is fixed, but the
	 * actual cut off for the continuous contract is computed every time because
	 * we have the cross over between the volumes
	 * 
	 * <p>
	 * This is a theoretical date, of course, because the actual start date is
	 * computed after.
	 * 
	 * @return the start date of this maturity, which is the start time after
	 *         which this maturity becomes "current".
	 * 
	 */
	public Date getStartDate() {
		return _getStartDateAsQuarter().getTime();

	}

	/**
	 * returns the start of the trading data; the trading data starts with daily
	 * data, of course, if the maturity is in the past, but this is only a
	 * <b>theoretical</b> date, the <b>actual</b> date is then filtered in
	 * derived classes.
	 * 
	 * 
	 * @return the theoretical starting date for this maturity, which is, in our
	 *         current setup, fifteen months before its expiration.
	 */
	public Date getStartTradingData() {
		Date exp = getExpirationDate();

		Calendar gc = Calendar.getInstance();
		gc.setTime(exp);

		gc.add(Calendar.MONTH, -15);

		return gc.getTime();
	}

	public int getYear() {
		return _year;
	}

	@Override
	public int hashCode() {

		return _year + _month * 4000;
	}

	/**
	 * Simple method to test if a maturity is a quarter maturity (used by the
	 * simulator to avoid simulating all months).
	 * 
	 * @return
	 */
	public boolean isAQuarterMaturity() {
		switch (_month) {
		case Calendar.MARCH:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
		case Calendar.DECEMBER:
			return true;
		default:
			return false;
		}
	}

	/**
	 * returns the data provider long string, used to make queries. for example
	 * "U2013".
	 * 
	 * <p>
	 * This string can change if the data provider changes, but the file string
	 * does NOT change because it is the string which is used to request data.
	 * 
	 * @return
	 */
	public String toDataProviderLongString() {
		return _getMonthString() + _year;
	}

	/**
	 * This method has been created only because in this way we don't have the
	 * need to create a different concrete class which handles the case of
	 * 2-digits year.
	 * 
	 * @return a string like "U13", to mean the current U maturity of the year
	 *         2013.
	 */
	public String toDataProviderMediumString() {
		synchronized (_ourFormat) {
			return _getMonthString() + _ourFormat.format(_year % 100);
		}

	}

	/**
	 * returns the data provider short string which is sometimes needed (for
	 * example eSignal distinguish this, but other providers may not)
	 */
	public String toDataProviderShortString() {
		return _getMonthString() + (_year % 10);
	}

	/**
	 * returns a file-friendly string used to build the name for the database.
	 * 
	 * <p>
	 * This string is data feed independent, because of course we don't want to
	 * change file names if we change the data provider.
	 * 
	 * <p>
	 * The string is dependent on the fact if this maturity is a quarter or a
	 * month maturity, because for historical reasons quarter maturites have
	 * been created before, and I did choose not to change their file name.
	 * 
	 * @return the string file name
	 */
	public String toFileString() {
		if (isAQuarterMaturity()) {
			return "" + _year + "-" + (_getQuarter() + 1) + "Q";
		}
		return "" + _year + "-" + _maturities[_month] + "M";

	}

	@Override
	public String toString() {
		return toFileString();
	}

}
