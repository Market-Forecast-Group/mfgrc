package com.mfg.strategy.builder.model;

import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.ManualEntryEvent;

public class ManualEntryEventModel extends SimpleEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getLabel() {
		return "Manual Entry";
	}
	
	@Override
	public EventGeneral exportMe() {
		return new ManualEntryEvent();
	}
}
