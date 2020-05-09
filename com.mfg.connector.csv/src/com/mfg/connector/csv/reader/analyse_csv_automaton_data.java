package com.mfg.connector.csv.reader;

import java.math.BigDecimal;

public class analyse_csv_automaton_data  {

	/**
	   Common data for all the automata
	 */
	public csv_automaton_data common_data;
	

	public parsed_csv_record previous_record = null;

	/**
	   This boolean is true when I have decided that this is a
	   range or time file.
	 */
	public boolean    decided_time_or_range = false;

	/**
	   How many times I have in the csv file different intervals,
	   this to decide if I have range or time bars.
	 */
	public int        time_bar_score = 0;


	public static final int RANGES = 20;

	/**
	   This stores some different ranges, used to compute the tick.
	 */
	public BigDecimal ranges[] = new BigDecimal[RANGES];

	/** This is simply the record number.*/
	public int rec_num = 0;

	/**
	   This is the output of this automaton
	 */
	public CsvAnalysis analysis_out;
}
