/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventAtomStopAndReverse extends EventAtomCommand {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;


	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		return true;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "Stop & R";
	}


	@Override
	public String getLabel() {
		return "STOP & REV.";
	}


	@Override
	public boolean ready2BChecked() {
		return true;
	}
	
	@Override
	public boolean isPure(boolean entry){
		return !entry;
	}

}
