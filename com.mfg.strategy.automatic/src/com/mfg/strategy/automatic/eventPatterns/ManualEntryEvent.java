package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class ManualEntryEvent extends EventAtom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getHtmlBody(HtmlUtils util) {
		return getLabel();
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		int entries = getEventsDealer().getManualEntries();
		if (entries>0){
			getEventsDealer().decreaseManualEntries();
			setTriggered(true);
			return true;
		}
		return false;
	}

	@Override
	public String getLabel() {
		return "Manual Entry";
	}
	
	@Override
	public boolean gotEntry() {
		return isTriggered();
	}

	@Override
	public int getScaleTo(EventGeneral requester) {
		return -1;
	}

	@Override
	public int getBigEntryScale() {
		return -1;
	}

}
