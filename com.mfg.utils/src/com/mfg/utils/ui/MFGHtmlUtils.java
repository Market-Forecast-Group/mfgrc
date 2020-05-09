package com.mfg.utils.ui;

import java.awt.Color;

public class MFGHtmlUtils extends HtmlUtils {

	public MFGHtmlUtils() {
		super();
	}

	public MFGHtmlUtils(boolean aIsOn, boolean aIsMultiline) {
		super(aIsOn, aIsMultiline);
	}

	public MFGHtmlUtils(boolean aIsOn) {
		super(aIsOn);
	}

	public String getPriceTimeHtmlString(double price, long time){
		return "("+color(time+"", Color.BLUE)+","+color(price+"", Color.RED)+")";
	}
	
	
}
