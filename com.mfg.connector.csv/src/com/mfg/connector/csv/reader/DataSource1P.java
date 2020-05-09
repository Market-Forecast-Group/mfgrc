package com.mfg.connector.csv.reader;

import java.util.ArrayList;

import com.mfg.common.Tick;

/**
 * This is a struct which is used to collect data and ticks from the csv.
 * <p>
 * 
 * Former it was used also in other parts of the application but now this is
 * only used to read a CSV file.
 * 
 * @author Sergio
 * 
 */
public class DataSource1P {

	public DataSource dsc = new DataSource();
	public ArrayList<Tick> ticks = new ArrayList<>();

	@Override
	public String toString() {
		return "I have " + ticks.size() + " ticks";
	}

}
