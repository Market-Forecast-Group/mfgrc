package com.mfg.chart.ui;

public enum ScrollingMode {
	SCROLLING, ZOOMING_OUT, NONE;

	public ScrollingMode swapScrolling() {
		return this == SCROLLING ? NONE : SCROLLING;
	}

	public ScrollingMode swapZoomingOut() {
		return this == ZOOMING_OUT ? NONE : ZOOMING_OUT;
	}
}
