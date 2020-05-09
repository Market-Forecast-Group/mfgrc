package com.mfg.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mfg.utils.PriceUtils;

/**
 * The unparsed bar is a bar which has unparsed (string) prices. It is here
 * because we don't yet know the tick size and the scale...
 * 
 * <p>
 * Added the volume information, because we have to know the volume to know when
 * to switch the contracts.
 * 
 * @author Pasqualino
 * 
 */
public class UnparsedBar {

	public final String open_s;
	public final String low_s;
	public final String high_s;
	public final String close_s;

	public final long start;

	public final int volume;

	private static SimpleDateFormat format = new SimpleDateFormat(
			"MM/dd/yy,HH:mm:ss");

	private static SimpleDateFormat formatToSend = new SimpleDateFormat(
			"MM/dd/yy,HH:mm:ss");

	public UnparsedBar(String date, String time, String os, String hs,
			String ls, String cs, int aVolume) throws ParseException {

		String date_time = date + "," + time;
		this.start = format.parse(date_time).getTime();

		this.open_s = os;
		this.low_s = ls;
		this.high_s = hs;
		this.close_s = cs;
		this.volume = aVolume;
	}

	public UnparsedBar(long aStart, String os, String hs, String ls, String cs,
			int aVolume) {
		this.start = aStart;
		this.open_s = os;
		this.low_s = ls;
		this.high_s = hs;
		this.close_s = cs;
		this.volume = aVolume;
	}

	public UnparsedBar(Bar aBar, int scale) {
		this.start = aBar.getTime();
		this.open_s = PriceUtils.longToString(aBar.getOpen(), scale);
		this.high_s = PriceUtils.longToString(aBar.getHigh(), scale);
		this.low_s = PriceUtils.longToString(aBar.getLow(), scale);
		this.close_s = PriceUtils.longToString(aBar.getClose(), scale);
		this.volume = aBar.getVolume();
	}

	@Override
	public String toString() {
		return getCsvRepresentation();
	}

	public String getCsvRepresentation() {

		String line = formatToSend.format(new Date(start)) + "," + open_s + ","
				+ high_s + "," + low_s + "," + close_s + " vol: " + volume;
		return line;
	}
}
