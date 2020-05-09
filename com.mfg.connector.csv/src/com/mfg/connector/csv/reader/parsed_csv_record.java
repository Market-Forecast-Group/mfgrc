package com.mfg.connector.csv.reader;

import java.math.BigDecimal;

public class parsed_csv_record implements Cloneable{

	public long instant;
	public BigDecimal   open;
	public BigDecimal   high;
	public BigDecimal   low;
	public BigDecimal   close;

	@Override
	public parsed_csv_record clone(){
		try{
			parsed_csv_record clone = (parsed_csv_record)super.clone();
			return clone;
		} catch (CloneNotSupportedException e){
			return null;
		}
	}

	@Override
	public String toString(){
	    return "t= " + instant + " op " + open + " l " + low + " h " + high + " c " + close;
	}
}
