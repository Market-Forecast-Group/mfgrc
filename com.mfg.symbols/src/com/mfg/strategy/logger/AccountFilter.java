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
package com.mfg.strategy.logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.OrderMessage;
import com.mfg.logger.ILogFilter;
import com.mfg.logger.ILogRecord;

/**
 * @author arian
 * 
 */
public class AccountFilter implements ILogFilter {

	private final Set<EAccountRouting> acceptedAccounts;

	public AccountFilter(Collection<EAccountRouting> aAcceptedAccounts) {
		this.acceptedAccounts = new HashSet<>(aAcceptedAccounts);
	}

	@Override
	public boolean accept(ILogRecord record) {
		Object msg = ((TradeMessageWrapper) record.getMessage())
				.getTradeMessage();
		if (msg instanceof OrderMessage) {
			OrderMessage orderMsg = (OrderMessage) msg;
			if (!acceptedAccounts.contains(orderMsg.getOrderRouting())) {
				return false;
			}
		}
		return true;
	}
}
