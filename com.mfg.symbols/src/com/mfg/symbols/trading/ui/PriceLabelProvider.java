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

import java.math.BigDecimal;
import java.math.BigInteger;

import com.mfg.strategy.logger.TradeMessageWrapper;

/**
 * @author arian
 * 
 */
public class PriceLabelProvider extends StrategyLogLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.logger.ui.StrategyLogLabelProvider#getText(com.mfg.strategy
	 * .logger.TradeMessageWrapper)
	 */
	@Override
	public String getText(TradeMessageWrapper msg) {
		int scale = msg.getTradeMessage().getTickScale();
		BigDecimal num = new BigDecimal(new BigInteger(
				Integer.toString((int) msg.getPrice())), scale);
		return num.toString();
	}

}
