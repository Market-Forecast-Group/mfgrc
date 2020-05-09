package com.mfg.connector.csv.reader;

/**
 * This struct holds the data relative to the csv reader. This data will then
 * used to initialize and perform the reading process.
 */
public final class CsvReaderParams {

	@Override
	public String toString() {
		return "csv " + csv_file_name + " tick_size " + tick_size
				+ " forced_tick " + forced_tick + " output only close "
				+ output_only_close + " prices_scale " + prices_scale;
	}

	public String csv_file_name;

	/**
	 * The tick size, default is 25, the tick size of the E-Mini S&P
	 */
	public int tick_size = 25;

	/**
	 * This is used to determine if the tick size is taken from the outside or
	 * not.
	 * 
	 * If this is true the analysis is skipped, but we may have some problems
	 * during reading if the file is not coherent with the tick_size and
	 * prices_scale parameters.
	 */
	public boolean forced_tick = false;

	/**
	 * This boolean controls whether we have to output only the close price of
	 * each bar. This is used in time strategies. Usually it is false.
	 */
	public boolean output_only_close = false;

	/**
	 * The price scale. Default is 0, that is we have integers.
	 */
	public int prices_scale = 0;

}