package com.mfg.connector.csv.reader;

public class csv_automaton_data {

	csv_record cur_record;
	parsed_csv_record parsed_record;
	public Object private_automaton_data;
	public process_csv_record proc_rec_func;
	public finalise_csv_reading finalize_func;
	public read_csv_record read_csv_func;
	public parse_csv_record parse_csv_func;
	public int rec_num = 0;

	public csv_automaton_data(csv_record cur_rec, 
			parsed_csv_record parsed_rec) {
		 this.cur_record = cur_rec;
		 this.parsed_record = parsed_rec;
	}

}
