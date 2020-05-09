package com.mfg.common;

import com.mfg.utils.U;

/**
 * The event which signals the stopping of a subscription.
 * 
 * <p>
 * This event is sent in two different cases:
 * 
 * <li>An external actor stops a data source which has not yet finished the warm
 * up or a real time data source (which is not stopped automatically
 * 
 * <li>A database subscription (that is a virtual symbol attached to a request
 * which does not continue in real time) has finished, and of course it won't
 * continue, because the virtual symbol has no more prices to send. This is more
 * or less a forced stop.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class DFSStoppingSubscriptionEvent extends DFSSymbolEvent {

	public DFSStoppingSubscriptionEvent(String aSymbol) {
		super(aSymbol);

	}

	@Override
	public String toPayload() {
		return U.join(this.symbol, STOPPING_SUBSCRIPTION_EVENT);
	}

}
