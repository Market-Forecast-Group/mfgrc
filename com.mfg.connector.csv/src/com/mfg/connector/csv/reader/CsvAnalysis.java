package com.mfg.connector.csv.reader;

import java.math.BigDecimal;

public class CsvAnalysis {

	public int computed_tick_size 	= 1;
	public int computed_scale 		= -1;
	public boolean is_time_bar 		= false;
	public long interval 			= -1;
	public BigDecimal minimumGap = null;
	public BigDecimal current_range = null;
    public int numRecords;
	
	@Override
    public String toString(){
	    return " is_time " + is_time_bar + " computed_tick " + computed_tick_size +
		" \ncomputed_scale " + computed_scale + " interval " + interval + 
		" minimumGap " + minimumGap + " range " + current_range + " num records " + numRecords;
	}

}
