package com.mfg.strategy.automatic.exportIndicator;

import com.mfg.interfaces.indicator.IIndicator;

public abstract class IndicatorParameter {
	private boolean included;
	private String shortName;
	private String longName;
	public IndicatorParameter(String aShortName, String aLongName) {
		super();
		this.shortName = aShortName;
		this.longName = aLongName;
	}
	public abstract String export(IIndicator indicator, int scale);
	public boolean isIncluded() {
		return included;
	}
	public void setIncluded(boolean aIncluded) {
		this.included = aIncluded;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String aShortName) {
		this.shortName = aShortName;
	}
	public String getLongName() {
		return longName;
	}
	public void setLongName(String aLongName) {
		this.longName = aLongName;
	}
	@Override
	public String toString() {
		if (included)
			return shortName;
		return "";
	}
	
	
	
}
