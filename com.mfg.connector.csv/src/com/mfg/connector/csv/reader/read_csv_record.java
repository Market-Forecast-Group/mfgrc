package com.mfg.connector.csv.reader;

import java.io.IOException;

import com.csvreader.CsvReader;

public interface read_csv_record {
	public void f(CsvReader a_reader, Object csv_record) throws IOException;
}
