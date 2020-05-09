package com.marketforecastgroup.dfsa.database;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DFSAUtils {

	public static final String TIME_1 = "yyyy.MM.dd hh:mm.ss";

	public static final String TIME_2 = "yyyy-MM-dd_hh-mm";


	public static String format(final long time, final String pattern) {
		Format formatter = new SimpleDateFormat(pattern);
		return formatter.format(new Date(time));
	}
}