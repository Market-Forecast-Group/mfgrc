package com.mfg.connector.csv.reader;

public class csv_record implements has_time_part {
	csv_time_part date_time = new csv_time_part();
	public String open;
	public String high;
	public String low;
	public String close;

	@Override
	public csv_time_part get_tp() {
		return date_time;
	}
}
