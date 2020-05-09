package com.mfg.connector.csv.reader;

import java.text.SimpleDateFormat;

public interface parse_csv_record {
	public void f(Object a_csv_rec, Object a_parsed_rec, SimpleDateFormat sdf) 
		    throws java.text.ParseException;
}
