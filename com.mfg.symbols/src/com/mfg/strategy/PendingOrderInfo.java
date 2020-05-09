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
package com.mfg.strategy;

import com.mfg.broker.IOrderMfg;

/**
 * @author arian
 * 
 */
public class PendingOrderInfo {
	private final long time;
	private final IOrderMfg order;

	public PendingOrderInfo(long aTtime, IOrderMfg aOrder) {
		super();
		this.time = aTtime;
		this.order = aOrder;
	}

	public long getTime() {
		return time;
	}

	public IOrderMfg getOrder() {
		return order;
	}

}
