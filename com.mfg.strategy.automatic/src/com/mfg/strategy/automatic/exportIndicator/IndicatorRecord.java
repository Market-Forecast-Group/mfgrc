package com.mfg.strategy.automatic.exportIndicator;

import java.util.ArrayList;
import java.util.List;

public class IndicatorRecord {
	private List<String> eventsData;
	private String pivotData = "0";
	private long time;
	public IndicatorRecord(long aTime) {
		super();
		this.time = aTime;
		eventsData =  new ArrayList<>();
	}
	
	public void addEventData(String data){
		eventsData.add(data);
	}
	
	public String toLine() {
		String res = "";
		for (String data : eventsData) {
			res+=(data+", ");
		}
		return res+pivotData;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long aTime) {
		this.time = aTime;
	}

	public String getPivotData() {
		return pivotData;
	}

	public void setPivotData(String aPivotData) {
		this.pivotData = aPivotData;
	}
	
	
	
}
