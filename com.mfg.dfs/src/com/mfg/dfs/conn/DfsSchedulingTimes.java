package com.mfg.dfs.conn;

import java.util.ArrayList;
import com.thoughtworks.xstream.XStream;

/**
 * holds the data about the scheduling times.
 * 
 * <p>
 * For now the scheduling times are a simple list of times in the form of
 * strings, formatted as "HH:mm:ss".
 * 
 * <p>
 * They represent the times of the day when the system will update the tables
 * even if there is noone listening
 * 
 * @author Sergio
 * 
 */
public class DfsSchedulingTimes {

	/**
	 * The array of schedulings. The schedulings are simple strings in the form
	 * of "hh:mm:ss", which will then be converted in a normal java date.
	 */
	public ArrayList<String> schedulings;
	
	@Override
	public String toString(){
		XStream xstream = new XStream();
		String xml = xstream.toXML(this);
		return xml;
	}

}
