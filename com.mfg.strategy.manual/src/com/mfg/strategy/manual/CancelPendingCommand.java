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
package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;

/**
 * @author arian
 * 
 */
public class CancelPendingCommand extends Command {
	public static CancelPendingCommand createCancelLongPendingCommand(
			ManualStrategySettings settings) {
		return new CancelPendingCommand(Routing.LONG, false, settings);
	}

	public static CancelPendingCommand createCancelShortPendingCommand(
			ManualStrategySettings settings) {
		return new CancelPendingCommand(Routing.SHORT, false, settings);
	}

	/**
	 * @param settings
	 * @return
	 */
	public static Command createCancelAllPendingCommand(
			ManualStrategySettings settings) {
		return new CancelPendingCommand(Routing.AUTO, false, settings);
	}

	private final boolean open;

	/**
	 * @param routing
	 * @param aOpen
	 * @param settings
	 */
	public CancelPendingCommand(Routing routing, boolean aOpen,
			ManualStrategySettings settings) {
		super(routing, settings);
		this.open = aOpen;
	}

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}
}
