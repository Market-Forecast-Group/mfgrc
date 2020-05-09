package com.mfg.interfaces.trading;

public enum ComputationType {
	Sm1Ratio("Sw0/Sw-1"),
	S1stRatio("Sw0''/Sw0'"),
	S2ndTicks("Sw0'' ticks");
	private String s;
	
	private ComputationType(String aS) {
		this.s = aS;
	}

	public String toRString() {
		return s;
	}
}
