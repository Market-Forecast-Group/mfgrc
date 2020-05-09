/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.trading.ui;

import com.mfg.broker.events.TradeMessage;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ui.LogColumnLabelProvider;
import com.mfg.strategy.logger.TradeMessageWrapper;

/**
 * @author arian
 * 
 */
public abstract class StrategyLogLabelProvider extends LogColumnLabelProvider {
	@Override
	public String getText(Object element) {
		TradeMessageWrapper msg = getMessage(element);
		if (msg.getType() == TradeMessage.WHITE_COMMENT) {
			return "";
		}
		return getText(msg);
	}

	/**
	 * @param element
	 * @return
	 */
	protected final static TradeMessageWrapper getMessage(Object element) {
		return (TradeMessageWrapper) ((ILogRecord) element).getMessage();
	}

	public abstract String getText(TradeMessageWrapper msg);
}
