package com.mfg.dfs.iqfeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * a date which has time zone information.
 * 
 * <p>This class has been necessary because all internal times in DFS are in UTC coordinates
 * but from iqFeed they arrive with a different time zone (EST).
 * 
 * <p>DFS in this case should have the possibility to parse the date and then to store it
 * in UTC coordinates.
 * 
 * @author Sergio
 *
 */
public class IqDate {

	private static SimpleDateFormat _ourFormat;
	private static SimpleDateFormat _ourOnlyDateFormat;
	//This is the format used with tick data: it has also the milliseconds.
	private static SimpleDateFormat _ourTickDateFormat;
	private static SimpleDateFormat _ourTickOnlyTimeFormat; //tick format, only time with milliseconds.
	
	//this is the format used to format the dates for the history requests.
	private static SimpleDateFormat _historyFormat;
	private static SimpleDateFormat _onlyDateHistoryFormat;
	
	private static SimpleDateFormat _utcFormat;
	
	static
	{
		_ourFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		_ourFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); //the time zone of iqfeed.
		
		_ourOnlyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		_ourOnlyDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		
		_ourTickDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		_ourTickDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); //the time zone of iqfeed.
		
		_ourTickOnlyTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		_ourTickOnlyTimeFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); //the time zone of iqfeed.
		
		_historyFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
		_historyFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); //the time zone of iqfeed.
		
		_onlyDateHistoryFormat = new SimpleDateFormat("yyyyMMdd");
		_onlyDateHistoryFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); //the time zone of iqfeed.
		
		_utcFormat = new SimpleDateFormat();
		_utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	
	//Just a test method to list all the available time zones.
	public static void main(String args[]){
		String[] availTZ = TimeZone.getAvailableIDs();
		for (String st : availTZ){
			System.out.println("Available> " + st);
		}		
	}
	
	/**
	 * tries to synchronizes a string which is a iqFeed TimeStamp.
	 * <p>It does not try to handle errors which are returned intactly to the caller.
	 * @param aDate
	 * @return a date which is in the iq time zone.
	 * @throws ParseException
	 */
	public static synchronized Date parseIqDate(String aDate) throws ParseException{
		Date date = _ourFormat.parse(aDate);
		return date;
	}
	
	public static synchronized String parseIqDateToUtc(String aDate) throws ParseException{
		Date date = _ourFormat.parse(aDate);		
		String utcDate = _utcFormat.format(date);
		return utcDate;
	}
	
	public static Date parseIqOnlyDate(String timeStampOrError) throws ParseException {
		Date date = _ourOnlyDateFormat.parse(timeStampOrError);
		return date;
	}

	public static Date parseTickDate(String aDateTime) throws ParseException {
		Date date = _ourTickDateFormat.parse(aDateTime);
		return date;
	}
	
	public static String formatToHistory(Date date) {
		return _historyFormat.format(date);
	}
	
	public static Date parseFromHistory(String aDateTime) throws ParseException{
		Date date = _historyFormat.parse(aDateTime);
		return date;
	}

	public static String formatToHistoryDate(Date date) {
		return _onlyDateHistoryFormat.format(date);
	}

	public static Date parseOnlyTimeMs(String aTime) throws ParseException {
		Date date = _ourTickOnlyTimeFormat.parse(aTime);
		return date;
	}
	
	public static Date parseOnlyTimeMsToday(String aTime) throws ParseException {
		return parseOnlyTimeMs(aTime, new GregorianCalendar());
	}
	
	/**
	 * parses a time only string and sets the day/month/year values from another calendar.
	 * 
	 * <p>It is useful when we receive from iqFeed a string which has only the time information
	 * and when we have to parse it according to a certain date.
	 * 
	 * @param aTime
	 * @param aDate the date will be changed!
	 * 
	 * @return the date parsed according also to the calendar given
	 * 
	 * @throws ParseException
	 */
	public static Date parseOnlyTimeMs(String aTime, Calendar aDate) throws ParseException{
		
		Date date = _ourTickOnlyTimeFormat.parse(aTime);
		//I have to set the date!
		
		GregorianCalendar gc1 = (GregorianCalendar) aDate.clone(); //I clone it to retain the date information
		
		aDate.setTime(date);
		
		boolean advance = false;
		boolean decrement = false;
		if(aDate.get(Calendar.DATE) == 2){
			//I have advanced one day, because of time zone...
			advance = true;
		}
		if (aDate.get(Calendar.DATE) == 31){
			decrement = true;
		}
		
		aDate.set(Calendar.YEAR, gc1.get(Calendar.YEAR));
		aDate.set(Calendar.MONTH, gc1.get(Calendar.MONTH));
		aDate.set(Calendar.DAY_OF_MONTH, gc1.get(Calendar.DAY_OF_MONTH));
		
		if (advance){
			aDate.roll(Calendar.DAY_OF_MONTH, true);
		}
		
		if (decrement){
			aDate.roll(Calendar.DAY_OF_MONTH, false);
		}
		
		return aDate.getTime();
		
	}	
}
