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

import com.mfg.strategy.logger.TradeMessageWrapper;

/**
 * @author arian
 * 
 */
public class ShortCapitalLabelProvider extends StrategyLogLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.logger.ui.StrategyLogLabelProvider#getText(com.mfg.strategy
	 * .logger.TradeMessageWrapper)
	 */
	@Override
	public String getText(TradeMessageWrapper msg) {
		return "" + msg.getShortCapital();
	}

}
