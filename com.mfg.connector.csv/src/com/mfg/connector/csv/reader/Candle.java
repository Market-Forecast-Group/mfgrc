package com.mfg.connector.csv.reader;

import java.util.Date;

public class Candle {
	public long instant;
	public long duration; //needed only by the csv engine.
	public int o;
	public int h;
	public int l;
	public int c;

	/**
	   simple data constructor.
	 */
	public Candle(long instant1, int o1, int h1, int l1, int c1){
	    this.instant = instant1;
	    this.o = o1;
	    this.h = h1;
	    this.l = l1;
	    this.c = c1;
	}

	public Candle(){
	    this.instant = -1; //invalid candle.
	}

	/**
	   Simple deserialization.
	 */
	@Override
    public String toString(){
	    return new Date(instant).toString() + " o " + o + " h " + h + " l " + l + " c " + c;
	}
}
