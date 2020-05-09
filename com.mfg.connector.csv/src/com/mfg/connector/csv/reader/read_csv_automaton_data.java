package com.mfg.connector.csv.reader;

import com.mfg.common.Tick;

public class read_csv_automaton_data {

	public csv_automaton_data common_data;
	public CsvAnalysis analysis;
	public DataSource1P data_out;
	public CsvReaderParams r_pars;
	public BarDeserializator dbm;
	public parsed_csv_record previous_record = null;
	public long overall_duration;
	public int n_recs = 0;
	
	public Tick ticks[] = new Tick[4];

}
